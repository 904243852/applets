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
        <name>Java安全编码规范：禁止使用不安全随机数</name>
        <regex>
            <![CDATA[java\.util\.Random;]]>
        </regex>
        <filetype>.java</filetype>
        <description>
            参考 http://www.cnblogs.com/rupeng/p/3723018.html
        </description>
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
        filetype = node_rule.getElementsByTagName('filetype')[0].firstChild.data
        description = node_rule.getElementsByTagName('description')[0].firstChild.data
        # print name, regex, filetype, description

        pattern = re.compile(regex, re.MULTILINE|re.DOTALL)
        rules_list.append((name, pattern, filetype, description))

for parent, dirnames, filenames in os.walk(directory): # 分别返回父目录、所有文件夹名字（不含路径）、所有文件名字
    for filename in filenames:
        filepath = os.path.join(parent, filename)
        # print "file found: " + filepath + "(" + os.path.abspath(filepath) + ")"

        file = open(filepath)
        fileContent = file.read()

        for rule in rules_list:
            if rule[2] == os.path.splitext(filepath)[1]:
                pattern = rule[1]

                for m in pattern.finditer(fileContent):
                    print '[%s]: %s was found in %s at line %d.' % (rule[0], m.group(), filepath, len(pattern_line.findall(fileContent, 0, m.start(0))) + 1)
