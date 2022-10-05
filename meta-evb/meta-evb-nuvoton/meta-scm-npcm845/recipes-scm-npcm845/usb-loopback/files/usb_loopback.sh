#!/bin/bash

Usage(){
    echo `basename $0` "[mount point]"
    echo "Test UDC by read write it as USB mass storage device"
    echo "The UDC must connect to test PC, and mount it at"
    echo "[mount point], so we can access it."
    echo "Note:"
    echo "  This script should \"not\" run on DUT"
    exit 1
}

get_checksum(){
    md5sum ${1} | awk '{print $1}'
}

log_stat(){
    number_of_tests_run=$(($number_of_tests_run + 1))
    run_end_time=$(date +%s);
    seconds_passed=$(($run_end_time-$run_start_time))
    echo " number of tests run $number_of_tests_run , failed $number_of_tests_failed" > $results_log_file
    minutes_passed="$(($seconds_passed / 60))"
    hours_passed="$(($minutes_passed / 60))"
    log_timestamp="$hours_passed hours $((minutes_passed % 60)) minutes and $(($seconds_passed % 60)) seconds elapsed."
    echo "$log_timestamp" >> $results_log_file
    echo "$result_log" >> $results_log_file
}

if [ -z "$1" ];then
    Usage
fi

#initialize  variables
mmcdev=$1
mount_path=$2
test_path="${mount_path}/udc"
test_file="dd_test"
random_data="/tmp/dd_file"
dd_read="/tmp/dd_read"
run_start_time=$(date +%s);
number_of_tests_run=0
number_of_tests_failed=0
result_log=PASS
results_log_file="/tmp/udc_stress.stat"

echo "Statefile: $results_log_file"

if [ ! -e ${mmcdev} ];then
    echo "cannot find /dev/sda"
    exit 1
fi

# fdisk
fdisk ${mmcdev} <<EOF
n
p
1
w
EOF

sleep 1
mke2fs ${mmcdev}

mkdir -p ${mount_path}
sleep 1
mount ${mmcdev} ${mount_path}

# Test we have permission to access the folder
if [ ! -d ${test_path} ];then
    mkdir -p ${test_path}
    if [ ! -d ${test_path} ];then
        echo "cannot create folder: ${test_path}"
        exit 1
    fi
    sleep 1
    touch ${test_path}/${test_file}
    sleep 1
fi

# clean stat file
echo > $results_log_file
# generate random data
dd if=/dev/urandom of=${random_data} bs=1M count=100
check=`get_checksum ${random_data}`
echo "generate data: $check"
# copy data to UDC
dd if=${random_data} of=${test_path}/${test_file} bs=1M count=100
check_w=`get_checksum ${test_path}/${test_file}`
if [ "${check}" != "${check_w}" ];then
    echo "check sum not match $check_w"
    exit 1
fi
# only while read data
while [ ! -f /tmp/stop_stress_test ]; do

    log_stat

    # read data from UDC
    dd if=${test_path}/${test_file} of=${dd_read} bs=1M count=100 2> /dev/null
    check_r=`get_checksum ${dd_read}`
    if [ "${check}" != "${check_r}" ];then
        echo "check sum not match while test reading $check_w"
        result_log=FAIL
        number_of_tests_failed=$(($number_of_tests_failed + 1))
        #exit 1
    fi
    rm ${dd_read}
done
exit 0
