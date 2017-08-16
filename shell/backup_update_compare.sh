#!/bin/bash

# 本脚本用于备份目录、更新目录下所有 git 工程、导出两个版本之间所有差异内容

current_directory=$(cd `dirname $0`; pwd)

if [[ ! -n $1 ]]; then
    source_directory=./Project
else
    source_directory=$1
fi

# # echo the last modify date
# index_file=$(find -name .git | head -n 1 | sed 's/.*/&\/index/')
# echo $(date) [info] The last modify date is $(stat ${index_file} | grep -i Modify | awk -F. '{print $1}' | awk '{print $2,$3}'), from the index file in .git: ${index_file}
# backup_directory_suffix=_$(stat ${index_file} | grep -i Modify | awk -F. '{print $1}' | awk '{print $2$3}'| awk -F- '{print $1$2$3}' | awk -F: '{print $1$2$3}')_backup

# echo the last commit date
cd $(find $source_directory -name .git | head -n 1)
last_commit_date=$(git log -1 | grep Date | cut -b 9-32)
last_commit_sha1=$(git log -1 | grep commit | cut -b 8-)
cd - > /dev/null
echo $(date) [info] The last commit date is $(date -d "$last_commit_date" +'%c'), and the SHA-1 is $last_commit_sha1
backup_directory_suffix=_$(date -d "$last_commit_date" +'%Y%m%d%H%M%S')_backup

# backup the source directory
backup_directory=${source_directory}$backup_directory_suffix
cp -R ${source_directory} ${backup_directory}
echo $(date) [info] Copy the source directory: ${source_directory} to the backup directory: ${backup_directory}

# update the source directory
for rop in $(find $source_directory -type d -name ".git")
do
    echo "\033[32mSwitch to path $(echo $rop | sed s'/\/.git//g') and do git pull.\033[0m"
    cd $rop/../ && git pull && cd $current_directory
done

# compare
# diff -Nur ${backup_directory} ${source_directory} | grep -E '^[^ ]' > report.txt