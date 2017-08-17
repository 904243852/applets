#!/bin/bash

# 本脚本用于备份目录、更新目录下所有 git 工程、导出两个版本之间所有差异内容

current_directory=$(cd `dirname $0`; pwd)

log_file=operation.log

if [[ ! -n $1 ]]; then
    source_directory=./Project
else
    source_directory=$1
fi

# echo the last modify date
fetch_head_file=$(find -name .git | head -n 1 | sed 's/.*/&\/FETCH_HEAD/')
echo $(date) [info] Start to process, sampling the fetch head file $fetch_head_file, the last modify date is $(stat ${fetch_head_file} | grep -i Modify | awk -F. '{print $1}' | awk '{print $2,$3}'). >> $log_file
backup_directory_suffix=_$(stat ${fetch_head_file} | grep -i Modify | awk -F. '{print $1}' | awk '{print $2$3}'| awk -F- '{print $1$2$3}' | awk -F: '{print $1$2$3}')_backup

# # echo the last commit date
# cd $(find $source_directory -name .git | head -n 1)
# repo_remote_url=$(git config --local --list | grep remote.origin.url | cut -b 19-)
# last_commit_date=$(git log -1 | grep Date | cut -b 9-32)
# last_commit_sha1=$(git log -1 | grep commit | cut -b 8-)
# cd - > /dev/null
# echo $(date) [info] Start to process, sampling the first repo $repo_remote_url, the last commit date is $(date -d "$last_commit_date" +'%c'), and the SHA-1 is $last_commit_sha1. >> $log_file
# backup_directory_suffix=_$(date -d "$last_commit_date" +'%Y%m%d%H%M%S')_backup

# backup the source directory
backup_directory=${source_directory}$backup_directory_suffix
cp -R ${source_directory} ${backup_directory}
echo $(date) [info] Copy the source directory: ${source_directory} to the backup directory: ${backup_directory} >> $log_file

# update the source directory
for rop in $(find $source_directory -type d -name ".git")
do
    # echo -e "\tSwitch to path $(echo $rop | sed s'/\/.git//g') and do git pull."
    cd $rop/../
    git pull
    if [[ $? -ne 0 ]]; then
        echo $(date) [info] Git pull from $(git config --local --list | grep remote.origin.url | cut -b 19-) failed. >> $log_file
    else
        echo $(date) [info] Git pull from $(git config --local --list | grep remote.origin.url | cut -b 19-) successfully, the latest commit SHA-1 is $(git log -1 | grep commit | cut -b 8-) during $(git log -1 | grep Date | cut -b 9-32). >> $log_file
    fi
    cd - > /dev/null
done

cd $current_directory

# compare
diff_report_file=diff_$(echo $backup_directory_suffix | cut -d '_' -f2).txt
diff -Nur ${backup_directory} ${source_directory} | grep -E '^[^ ]' > $diff_report_file

echo -e "$(date) [info] Finish to process, the backup is ${backup_directory}, the difference report is $diff_report_file.\n" >> $log_file