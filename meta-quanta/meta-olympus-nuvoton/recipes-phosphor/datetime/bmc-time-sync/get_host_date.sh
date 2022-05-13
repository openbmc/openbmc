#!/bin/bash

# set up start date
start=$(date -d "1970-01-01" +%s)

get_date(){
    ds=`expr $1 + $start`
    if [ -z "$ds" ];then
        echo -e "\nnot valid date time!!\n"
        exit 1
    fi
    echo $(date -d @$ds)
}

ipmb_get_time(){
res=`busctl call xyz.openbmc_project.Ipmi.Channel.Ipmb \
  /xyz/openbmc_project/Ipmi/Channel/Ipmb \
  org.openbmc.Ipmb sendRequest yyyyay 1 0x0a 0 0x48 0`
if [ "$?" != "0" ];then
    echo "cannot get time from IPMB"
    res=""
fi
}

### main ###

ipmb_get_time
# echo $res
if [ -z "$res" ];then
    sleep 40
    ipmb_get_time
    if [ -z "$res" ];then
        echo "still cannot get time from IPMB, stop time sync process"
        exit 1
    fi
fi
# TODO: maybe add retry for get time later?
error=`echo $res |awk '{print ($2 + $6) }'`
if [ "$error" != "0" ];then
    echo "get time failed..."
    exit 1
fi
# convert response data as LSB to time as second from 1970/1/1
hosttime=`echo "$res"|awk 'n=lshift($11,24)+lshift($10,16)+lshift($9,8)+$8 {print n}'`

# echo "host times: $(get_date $hosttime)"

# set time
echo "set date time: "
date -s @$(expr $hosttime + $start)
if [ "$?" != "0" ];then
    ts=$(expr $hosttime + $start)
    echo "set BMC time to $(date -d @$ts) failed!"
    exit 1
fi
exit 0
