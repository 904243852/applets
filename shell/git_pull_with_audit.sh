#!/bin/bash

# 本脚本更新目录下所有 git 工程，记录日期版本号，以及导出两个版本之间所有差异内容，用于代码增量审计工作

current_directory=$(cd `dirname $0`; pwd)

log_file=$current_directory/operation.log

if [[ ! -n $1 ]]; then
    source_directory=./Project
else
    source_directory=$1
fi

for rop in $(find $source_directory -type d -name ".git")
do
    cd $rop/../

    repo_remote_url=$(git config --local --list | grep remote.origin.url | cut -b 19-)
    last_commit_date=$(git log -1 | grep Date | cut -b 9-32)
    last_commit_sha1=$(git log -1 | grep commit | cut -b 8-)
    echo $(date) [info] Check the repo $repo_remote_url, the last commit date is $last_commit_date with the SHA-1 is $last_commit_sha1. >> $log_file

    git pull
    if [[ $? -ne 0 ]]; then
        echo $(date) [info] Git pull from $repo_remote_url failed. >> $log_file
    else
        diff_report_file=difference.$(date +'%Y%m%d%H%M%S').txt
        git diff $last_commit_sha1 $(git log -1 | grep commit | cut -b 8-) > $diff_report_file
        echo $(date) [info] Git pull from $repo_remote_url successfully, the latest commit date is $(git log -1 | grep Date | cut -b 9-32) with thw SHA-1 is $(git log -1 | grep commit | cut -b 8-). The difference since $last_commit_sha1 ref to $diff_report_file. >> $log_file
    fi

    cd - > /dev/null
done