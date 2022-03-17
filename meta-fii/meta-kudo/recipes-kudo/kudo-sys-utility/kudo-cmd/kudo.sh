#!/bin/bash
# help information

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-kudo/recipes-kudo/kudo-fw-utility/kudo-fw/kudo-lib.sh
source /usr/libexec/kudo-fw/kudo-lib.sh

function usage_rst() {
  echo " kudo rst [parameter]"
  echo "        hotswap  --> reset the whole kudo node"
  echo "        system   --> reset the host"
  echo "        btn      --> trigger a power button event"
  echo "        shutdown --> send out shutdown signal to CPU"
  echo "        display  --> "
}

function usage_led() {
  echo " kudo led 'att'/'boot' [parameter]"
  echo "        on --> change to CPU console"
  echo "        off --> change to CPU 0 SCP console"
  echo "        status --> change to CPU 1 SCP console"
}

function usage_uart() {
  echo " kudo uart [parameter]"
  echo "        host --> show CPU console"
  echo "        scp --> show SCP0 console"
  echo "        swhost --> change to CPU console to ttyS1"
  echo "        swscp1 --> change to CPU 0 SCP console to ttyS3"
  echo "        swscp2 --> change to CPU 1 SCP console"
  echo "        swhosthr --> change CPU console to header"
  echo "        swscphr --> change SCP console to header"
  echo "        display  --> "
}

function usage() {
  echo " kudo BMC console system utilities"
  echo " kudo [optional] [parameter]"
  echo "   rst   --> reset traget device"
  echo "   fw    --> get version"
  echo "   uart  --> control the uart mux"
  echo "   led   --> control the leds"
}

function reset() {
  case $1 in
    hotswap)
      # Virtual reset #94
      set_gpio_ctrl 94 out 1
      ;;
    system)
      # S0 system reset #65
      set_gpio_ctrl 65 out 0
      sleep 1
      set_gpio_ctrl 65 out 1
      ;;
    btn)
      # power button #203
      set_gpio_ctrl 203 out 1
      sleep 1
      set_gpio_ctrl 203 out 0
      ;;
    shutdown)
      # BMC_CPU_SHD_REQ #70
      set_gpio_ctrl 70 out 0
      sleep 3
      set_gpio_ctrl 70 out 1
      ;;
    forceOff)
      # power button #203
      set_gpio_ctrl 203 out 1
      sleep 6
      set_gpio_ctrl 203 out 0
      ;;
    display)
      echo "Virtual reset   #94" "$(get_gpio_ctrl 94)"
      echo "S0 System reset #65" "$(get_gpio_ctrl 65)"
      echo "Power Button   #203" "$(get_gpio_ctrl 203)"
      echo "BMC_CPU SHD Req #70" "$(get_gpio_ctrl 70)"
      ;;
    *)
      usage_rst
    ;;
  esac
}

function fw_rev() {
  BMC_CPLD_VER_FILE="/run/cpld0.version"
  MB_CPLD_VER_FILE="/run/cpld1.version"

  cmd=$(cat ${BMC_CPLD_VER_FILE})
  echo " BMC_CPLD: " "${cmd}"
  cmd=$(cat ${MB_CPLD_VER_FILE})
  echo " MB_CPLD: " "${cmd}"

  # BMC Version

  # Save VERSION_ID line in string "VERSION_ID=vXX.XX-XX-kudo"
  StringVersion=$(awk '/VERSION_ID/' /etc/os-release)

  #Save Major Version value between v and . "vXX." then convert Hex to Decimal
  MajorVersion=${StringVersion#*v}
  MajorVersion=$(( 16#${MajorVersion%.*}))

  #Save SubMajor Version valeu between . and - ".XX-" then convert  Hex to Decimal
  SubMajorVersion=${StringVersion##*.}
  SubMajorVersion=$(( 16#${SubMajorVersion%%-*}))

  #Save Minor Version value between - and - "-XX-" then convert  Hex to Decimal
  MinorVersion=${StringVersion#*-}
  MinorVersion=$(( 16#${MinorVersion%-*}))

  echo " BMC: " ${MajorVersion}.${SubMajorVersion}.${MinorVersion}

  #BMC PWR Sequencer
  i2cset -y -f -a 14 0x59 0xfe 0x0000 w
  cmd=$(i2cget -y -f -a 14 0x59 0xfe i 2 | awk '{print substr($0,3)}')
  echo " BMC PowerSequencer : ${cmd}"
  #only display with smbios exists
  if [[ -e /var/lib/smbios/smbios2 ]]; then
    cmd=$(busctl introspect xyz.openbmc_project.Smbios.MDR_V2 \
            /xyz/openbmc_project/inventory/system/chassis/motherboard/bios | grep Version | awk '{print $4}')
    echo " Bios: $cmd"
  fi

  cmd=$(i2cget -f -y 2 0x4f 0x1 w);
  echo " SCP Firmware: ${cmd}"

  adm1266_ver  | grep REVISION

}

function uartmux() {
  case $1 in
    host)
      if [ "$(tty)" ==  "/dev/ttyS0" ]; then
        echo "Couldn't redirect to the host console within BMC local console"
      else
        echo "Entering Console use 'shift ~~..' to quit"
        obmc-console-client -c /etc/obmc-console/server.ttyS1.conf
      fi
      ;;
    scp)
      if [ "$(tty)" ==  "/dev/ttyS0" ]; then
        echo "Couldn't redirect to the scp console within BMC local console"
      else
        echo "Entering Console use 'shift ~~..' to quit"
        obmc-console-client -c /etc/obmc-console/server.ttyS3.conf
      fi
      ;;
    swhost)
      set_gpio_ctrl 167 out 1
      ;;
    swscp1)
      set_gpio_ctrl 161 out 1
      set_gpio_ctrl 177 out 1
      set_gpio_ctrl 198 out 0
      ;;
    swscp2)
      set_gpio_ctrl 161 out 1
      set_gpio_ctrl 177 out 1
      set_gpio_ctrl 198 out 1
      ;;
    swhosthr)
      set_gpio_ctrl 167 out 0
      ;;
    swscphr)
      set_gpio_ctrl 161 out 0
      set_gpio_ctrl 177 out 0
      ;;
    display)
      if [ "$(get_gpio_ctrl 167)" -eq 1 ]; then
        echo " CPU host to BMC console"
      else
        echo " CPU host to header"
      fi
      if [ "$(get_gpio_ctrl 161)" -eq 1 ] && [ "$(get_gpio_ctrl 177)" -eq 1 ]; then
        if [ "$(get_gpio_ctrl 198)" -eq 1 ]; then
          echo " SCP2 host to BMC console"
        else
          echo " SCP1 host to BMC console"
        fi
      elif [ "$(get_gpio_ctrl 161)" -eq 0 ] && [ "$(get_gpio_ctrl 177)" -eq 0 ]; then
        if [ "$(get_gpio_ctrl 198)" -eq 1 ]; then
          echo " SCP2 host to Header"
        else
          echo " SCP1 host to Header"
        fi
      else
        echo "It's unknown status"
        echo "167" "$(get_gpio_ctrl 167)"
        echo "161" "$(get_gpio_ctrl 161)"
        echo "177" "$(get_gpio_ctrl 177)"
        echo "198" "$(get_gpio_ctrl 198)"
      fi
      ;;
    *)
      usage_uart
      ;;
  esac
}

function ledtoggle() {

    CurrentLED=$( i2cget -y -f -a 34 0x76 0x05 i 1 | cut -d ' ' -f 2)
  case $1 in
    boot)
        cmd=$(((CurrentLED & 0x40) != 0))
        case $2 in
         on)
            #turn on LED
            if [[ $cmd -eq 0 ]]; then
                setValue=$(( 0x40 + CurrentLED ))
                i2cset -y -f -a 34 0x76 0x10 "$setValue"
            fi
            ;;
         off)
            #turn off led
            if [[ $cmd -eq 1 ]]; then
                setValue=$(( 0x80 & CurrentLED ))
                i2cset -y -f -a 34 0x76 0x10 "$setValue"
            fi
            ;;
        toggle)
            #turn on LED
                setValue=$(( 0x40 ^ CurrentLED ))
                i2cset -y -f -a 34 0x76 0x10 "$setValue"
            ;;
         status)
            #displayLED status
            if [[ $cmd -eq 1 ]]; then
                echo "on"
            else
                echo "off"
            fi
            ;;
         *)
            usage_led
            ;;
        esac
      ;;
    att)
        cmd=$(((CurrentLED & 0x80) != 0))
        case $2 in
         on)
            #turn on LED
            if [[ $cmd -eq 0 ]]; then
                setValue=$(( 0x80 + CurrentLED ))
                i2cset -y -f -a 34 0x76 0x10 "$setValue"
            fi
            ;;
         off)
            #turn off led
            if [[ $cmd -eq 1 ]]; then
                 setValue=$(( 0x40 & CurrentLED ))
                i2cset -y -f -a 34 0x76 0x10 "$setValue"
            fi
            ;;
        toggle)
            #turn on LED
            setValue=$(( 0x80 ^ CurrentLED ))
            i2cset -y -f -a 34 0x76 0x10 "$setValue"
            ;;
         status)
            #displayLED status
            if [[ $cmd -eq 1 ]]; then
                echo "on"
            else
                echo "off"
            fi
            ;;
         *)
            usage_led
            ;;
        esac
      ;;
    *)
        usage_led
        ;;
    esac
}

function usblist() {
  for i in {0..8}
  do
    cmd=$(devmem 0xf083"${i}"154)
    printf "udc%d : 0xF803%d154-" "${i}" "${i}"
    $cmd
   done
}

case $1 in
  rst)
    reset "$2"
    ;;
  fw)
    fw_rev
    ;;
  uart)
    uartmux "$2"
    ;;
  usb)
    usblist
    ;;
  led)
    ledtoggle "$2" "$3"
    ;;
  *)
    usage
    ;;
esac
