#!/usr/bin/env python
#coding=utf-8

import os
import os.path
import sys
import re
import xml.dom.minidom

rules_xml_content = '''<?xml version="1.0" encoding="UTF-8"?>
<rules>
    <rule>
        <name>Web渗透：SQL 注入（JDBC）</name>
        <regex>
            <![CDATA[createStatement|prepareStatement]]>
        </regex>
        <filetype>.java</filetype>
        <description>
            使用 JDBC 执行 SQL 语句时，禁止使用未校验的用户输入的数据拼接 SQL 语句
        </description>
    </rule>
    <rule>
        <name>Web渗透：XML 注入（XML外部实体注入，即XXE注入）</name>
        <regex>
            DocumentBuilderFactory|DocumentBuilder|SAXParserFactory|SAXParser|SAXBuilder|SAXReader
        </regex>
        <filetype>.java</filetype>
        <description>ref http://www.cnblogs.com/clearlove/articles/5502012.html，解决方法：禁止使用外部实体</description>
    </rule>
    <rule>
        <name>Web渗透：XPath 注入</name>
        <regex>javax\.xml\.xpath;</regex>
        <filetype>.java</filetype>
        <description></description>
    </rule>
    <rule>
        <name>Web渗透：日志注入</name>
        <regex>\.debug|\.info|\.warn|\.error</regex>
        <filetype>.java</filetype>
        <description></description>
    </rule>

    <rule>
        <name>Java安全编码规范：禁止使用不安全随机数</name>
        <regex>java\.util\.Random;</regex>
        <filetype>.java</filetype>
        <description>
            ref http://www.cnblogs.com/rupeng/p/3723018.html
        </description>
    </rule>
    <rule>
        <name>Java安全编码规范：禁止使用未加密的套接字</name>
        <regex>java\.net\.Socket;</regex>
        <filetype>.java</filetype>
        <description></description>
    </rule>
    <rule>
        <name>Java安全编码规范：Java异常中包含敏感信息</name>
        <regex>
            FileNotFoundException|JarException|MissResourceException|NotOwnerException|ConcurrentModificationException|InsufficientResourcesException|BindException|OutOfMemoryException|SQLException|StackOverflowException
        </regex>
        <filetype>.java</filetype>
        <description></description>
    </rule>

    <rule>
        <name>JS安全编码规范：eval等方法解析js脚本中，传入的参数未经js编码，可能导致XSS</name>
        <regex>eval|setTimeout</regex>
        <filetype>.js,.jsp,.tag</filetype>
        <description></description>
    </rule>
</rules>
'''

# 加载规则配置文件
# rules_dom = xml.dom.minidom.parse('rules.xml')
rules_dom = xml.dom.minidom.parseString(rules_xml_content)
# 需要扫描的目录，默认扫描当前目录
directory = sys.argv[1] if len(sys.argv) > 1 else './'

rules_list = []
pattern_line = re.compile(r'\n')

node_rules = rules_dom.documentElement
for node_rule in node_rules.childNodes:
    if node_rule.nodeType == node_rule.ELEMENT_NODE:
        name = node_rule.getElementsByTagName('name')[0].firstChild.data
        regex = node_rule.getElementsByTagName('regex')[0].firstChild.wholeText.strip()
        filetypes = re.split('\s|,|;|\|', node_rule.getElementsByTagName('filetype')[0].firstChild.data)
        if '' in filetypes:
            filetypes.remove('')
        node_description = node_rule.getElementsByTagName('description')[0].firstChild
        description = node_description.data if node_description != None else ''
        # print name, regex, filetypes, description

        pattern = re.compile(regex, re.MULTILINE|re.DOTALL)
        rules_list.append((name, pattern, filetypes, description))

for parent, dirnames, filenames in os.walk(directory): # 分别返回父目录、所有文件夹名字（不含路径）、所有文件名字
    for filename in filenames:
        filepath = os.path.join(parent, filename)
        # print "file found: " + filepath + "(" + os.path.abspath(filepath) + ")"

        file = open(filepath)
        fileContent = file.read()

        for rule in rules_list:
            if os.path.splitext(filepath)[1] in rule[2]:
                pattern = rule[1]

                for m in pattern.finditer(fileContent):
                    print '[%s]: %s was found in %s at line %d.' % (rule[0], m.group(), filepath, len(pattern_line.findall(fileContent, 0, m.start(0))) + 1)
