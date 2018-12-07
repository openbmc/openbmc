#!/bin/sh -e
# SOC update tool, need two parameter(soc update file name and TFTP ip)

if [ ! -n "$1" -o ! -n "$2" ] ;then
    echo "Please enter SOC-ImageName and TFTP-IP."
    echo "Format: socupdate.sh FileName IP"
    echo "Example: socupdate.sh 0ACJAXXX.ROM 192.168.1.1"
    exit
fi

echo "SOC update start."

echo "Check BMC status."
checkbmc='gpioutil -p Q4'
if $checkbmc = "1"; then
    echo "BMC is ready."
else
    echo "BMC isn't ready."
    exit
fi

echo "Check SOC image does it exist."
cd /tmp/
FILEEXIST=0
if [ -f "/tmp/$1" ]; then
    GETFILE=`du -k $1 | awk '{print $1}'`
    if [ "$GETFILE" -eq "65536" ]; then
        FILEEXIST=1
        echo "SOC image is exist."
    else
        FILEEXIST=0
    fi
fi

if [ $FILEEXIST -eq 0 ]; then
    echo "Get SOC image from TFTP server."
    echo "Please wait a few minutes."
    tftp -g -r $1 $2 &
    sleep 2
    LENGTH=0
    ERRCOUNT=0
    while true
    do
        echo -ne "\r["
        usleep 500000
        while [ $LENGTH -le 30 ]
        do
                usleep 500000
                echo -n ">"
                LENGTH=$(($LENGTH+1))
                if [ -f "/tmp/$1" ]; then
                    GETFILE=`du -k $1 | awk '{print $1}'`
                    if [ $GETFILE -eq 65536 ]; then
                        echo -en "\r"
                        echo -en "--- Image download completed --- \n"
                        usleep 1000000
                        break 2
                    fi
                else
                    ERRCOUNT=$(($ERRCOUNT+1))
                    if [ $ERRCOUNT -le 10 ]; then
                        echo -en "\r"
                        echo -en "SOC-ImageName or TFTP-IP is ERROR. \n"
                        usleep 500000
                        exit
                    fi
                fi
        done
        echo -n "]"
        LENGTH=0
        usleep 500000
        echo -en "\r                                "
    done
fi
sleep 3

echo "Switch the host SPI bus to BMC."
cmd='gpioutil -p C7 -v 1'
if $cmd 1>/dev/null 2>&1; then
    echo "Switch completed."
else
    echo "Switch failed."
fi
echo "Load the ASpeed SMC driver"
echo 1e630000.flash-controller > /sys/bus/platform/drivers/aspeed-smc/bind
sleep 2
chassisstate=$(obmcutil chassisstate | awk -F. '{print $NF}')
echo "---Current Chassisstate $chassisstate---"
if [ "$chassisstate" == 'On' ];
then
    echo "---Chassis on turning it off---"
    obmcutil chassisoff
    sleep 10
fi

echo "Flashcp to update SOC."
echo "Please wait a few minutes."
flashcp -v /tmp/$1 /dev/mtd6
sleep 3

echo "Switch the host SPI bus to HOST."
cmd1='gpioutil -p C7 -v 0'
if $cmd1 1>/dev/null 2>&1; then
    echo "Switch completed."
else
    echo "Switch failed."
fi
echo "unLoad the ASpeed SMC driver"
echo 1e630000.flash-controller > /sys/bus/platform/drivers/aspeed-smc/unbind
echo -ne "SOC update steps is complete.\n"
if [ "$chassisstate" == 'On' ];
then
    echo "5 sec later will be power-on."
    echo "If wantn't power-on, please CTRL + C leave script."
    WAITSEC=5
    while [ $WAITSEC -ge 0 ]
    do
        echo -n "Countdown seconds:$WAITSEC"
        sleep 1
        echo -en "\r"
        WAITSEC=$(($WAITSEC-1))
        if [ $WAITSEC -eq 0 ]; then
            echo -en "\r"
            echo -en "--- Power-on ---       \n"
            obmcutil chassison
            break 1
        fi
    done
fi
