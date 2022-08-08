#!/bin/bash
# help information

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-mori/recipes-mori/mori-fw-utility/mori-fw/mori-lib.sh
source /usr/libexec/mori-fw/mori-lib.sh

function usage_rst() {
  echo " mori rst [parameter]"
  echo "        hotswap  --> reset the whole mori node"
  echo "        system   --> reset the host"
  echo "        btn      --> trigger a power button event"
  echo "        shutdown --> send out shutdown signal to CPU"
  echo "        display  --> "
}

function usage_led() {
  echo " mori led 'attn'/'boot' [parameter]"
  echo "        on --> turn led on"
  echo "        off --> turn led off"
  echo "        toggle --> toggle led"
  echo "        status --> get status of led"
}

function usage_uart() {
  echo " mori uart [parameter]"
  echo "        host --> show CPU console"
  echo "        mpro --> show Mpro console"
  echo "        swhost --> change to CPU console to ttyS1"
  echo "        swmpro --> change to CPU 0 Mpro console to ttyS3"
  echo "        swhosthr --> change CPU console to header"
  echo "        swmprohr --> change Mpro console to header"
  echo "        display  --> "
}

function usage() {
  echo " mori BMC console system utilities"
  echo " mori [optional] [parameter]"
  echo "   rst   --> reset traget device"
  echo "   fw    --> get version"
  echo "   uart  --> control the uart mux"
  echo "   led   --> control the leds"
}

function reset() {
  case $1 in
    hotswap)
      # Virtual AC reset
      echo "mori.sh rst hotswap occurred"
      set_gpio_ctrl HOTSWAP 1
      ;;
    system)
      # S0 system reset
      set_gpio_ctrl S0_SYSRESET 0
      sleep 1
      set_gpio_ctrl S0_SYSRESET 1
      ;;
    btn)
      # virtual power button on
      set_gpio_ctrl POWER_OUT 0
      sleep 1
      set_gpio_ctrl POWER_OUT 1
      ;;
    shutdown)
      # request host shutdown
      set_gpio_ctrl S0_SHD_REQ  0
      sleep 3
      set_gpio_ctrl S0_SHD_REQ  1
      ;;
    forceOff)
      # virtual power button off
      set_gpio_ctrl POWER_OUT 0
      sleep 6
      set_gpio_ctrl POWER_OUT 1
      ;;
    display)
      echo "Virtual AC Reset:     GPIO$(get_gpio_num HOTSWAP)" "State:$(get_gpio_ctrl HOTSWAP)"
      echo "Virtual Power Button: GPIO$(get_gpio_num POWER_OUT)" "State:$(get_gpio_ctrl POWER_OUT)"
      echo "S0 System Reset:      GPIO$(get_gpio_num S0_SYSRESET)" "State:$(get_gpio_ctrl S0_SYSRESET)"
      echo "S0 Shutdown Request:  GPIO$(get_gpio_num S0_SHD_REQ)" "State:$(get_gpio_ctrl S0_SHD_REQ)"
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

  major=$(ipmitool mc info | grep "Firmware Revision" | awk '{print $4}')
  cmd=$(ipmitool mc info | tail -4 | tr -s '\t' ' ' | tr -s '\n' ' ')

  for hex in $cmd; do
    minor="${hex:2}$minor";
  done

  minor=$(echo "obase=10; ibase=16; ${minor^^}" | bc)
  echo " BMC        : " "${major}"."${minor}"

  #BMC PWR Sequencer
  i2cset -y -f -a "${I2C_BMC_PWRSEQ[0]}" 0x"${I2C_BMC_PWRSEQ[1]}" 0xfe 0x0000 w
  cmd=$(i2cget -y -f -a "${I2C_BMC_PWRSEQ[0]}" 0x"${I2C_BMC_PWRSEQ[1]}" 0xfe i 2 | awk '{print substr($0,3)}')
  echo " BMC PowerSequencer : ${cmd}"
  #only display with smbios exists
  if [[ -e /var/lib/smbios/smbios2 ]]; then
    cmd=$(busctl introspect xyz.openbmc_project.Smbios.MDR_V2 \
            /xyz/openbmc_project/inventory/system/chassis/motherboard/bios | grep Version | awk '{print $4}')
    echo " Bios: $cmd"
  fi

  adm1266_ver "${I2C_MB_PWRSEQ[0]}" | grep REVISION

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
    mpro)
      if [ "$(tty)" ==  "/dev/ttyS0" ]; then
        echo "Couldn't redirect to the Mpro console within BMC local console"
      else
        echo "Entering Console use 'shift ~~..' to quit"
        obmc-console-client -c /etc/obmc-console/server.ttyS3.conf
      fi
      ;;
    swhost)
      set_gpio_ctrl S0_UART0_BMC_SEL 1
      ;;
    swmpro)
      set_gpio_ctrl S0_UART1_BMC_SEL 1
      ;;
    swhosthr)
      set_gpio_ctrl S0_UART0_BMC_SEL 0
      ;;
    swmprohr)
      set_gpio_ctrl S0_UART1_BMC_SEL 0
      ;;
    display)
      if [ "$(get_gpio_ctrl S0_UART0_BMC_SEL)" -eq 1 ]; then
        echo " CPU host to BMC console"
      else
        echo " CPU host to header"
      fi

      if [ "$(get_gpio_ctrl S0_UART1_BMC_SEL)" -eq 1 ]; then
        echo " Mpro host to BMC console"
      else
        echo " Mpro host to header"
      fi
      ;;
    *)
      usage_uart
      ;;
  esac
}

function ledtoggle() {
  case $1 in
    boot)
      cmd=$(get_gpio_ctrl SYS_BOOT_STATUS_LED)
      case $2 in
        on)
          #turn on LED
          set_gpio_ctrl SYS_BOOT_STATUS_LED 1
          ;;
        off)
          #turn off LED
          set_gpio_ctrl SYS_BOOT_STATUS_LED 0
          ;;
        toggle)
          #toggle off LED
          if [[ $cmd -eq 1 ]]; then
            set_gpio_ctrl SYS_BOOT_STATUS_LED 0
          fi

          #toggle on LED
          if [[ $cmd -eq 0 ]]; then
            set_gpio_ctrl SYS_BOOT_STATUS_LED 1
          fi
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
    attn)
      cmd=$(get_gpio_ctrl SYS_ERROR_LED)
      case $2 in
        on)
          #turn on LED
          set_gpio_ctrl SYS_ERROR_LED 1
          ;;
        off)
          #turn off LED
          set_gpio_ctrl SYS_ERROR_LED 0
          ;;
        toggle)
          #toggle off LED
          if [[ $cmd -eq 1 ]]; then
            set_gpio_ctrl SYS_ERROR_LED 0
          fi

          #toggle on LED
          if [[ $cmd -eq 0 ]]; then
            set_gpio_ctrl SYS_ERROR_LED 1
          fi
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
  led)
    ledtoggle "$2" "$3"
    ;;
  *)
    usage
    ;;
esac

