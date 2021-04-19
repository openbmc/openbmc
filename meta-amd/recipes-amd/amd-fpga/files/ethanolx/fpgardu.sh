#!/bin/bash
echo
echo "-----FPGA Ethanol<x> CRB Register Dump Utility"
echo
I2CBUS=2
FPGAADDR=0x50

# FPGA FW Version Information
FPGA_REG=39
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
MAJOR=$((DATA >> 4))
MINOR=$((DATA & 0x0F))
echo FPGA FW Version: $MAJOR.$MINOR

# IP register information
FPGA_REG=0
IP_REG_MAX=3
printf "IP Address Registers: "
while [ $FPGA_REG -le $IP_REG_MAX ]
    do
    # not using printf as integer and hex values are the same for this use
    DATA=$(i2cget -y $I2CBUS $FPGAADDR $FPGA_REG)
    if [ $FPGA_REG -ne $IP_REG_MAX ] ; then
        printf "%d." $DATA
    else
        printf "%d\n\n" $DATA
    fi
    let FPGA_REG=FPGA_REG+1
    done

# VDD block - Addresses 16 - 23
FPGA_REG=16
VDD_REG_MAX=23
SOCKET=0

while [ $FPGA_REG -le $VDD_REG_MAX ]
    do
    VDD_LOOP_CNT=0

    while [ $VDD_LOOP_CNT -le 1 ]
        do
        if [ $VDD_LOOP_CNT -eq 0 ] ; then
            VDD_LOOP_CNT_TXT="Enables"
        else
            VDD_LOOP_CNT_TXT="Power Goods"
        fi
        DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
        echo ----------FPGAreg$FPGA_REG-----P$SOCKET VDD $VDD_LOOP_CNT_TXT
        echo VDD_18_DUAL : $((DATA & 0x01))
        echo VDD_SOC_DUAL: $((((DATA & 0x02)) >> 1))
        echo VDD_SPD_ABCD: $((((DATA & 0x04)) >> 2))
        echo VDD_VPP_ABCD: $((((DATA & 0x08)) >> 3))
        echo VDD_VTT_ABCD: $((((DATA & 0x10)) >> 4))
        echo VDD_MEM_ABCD: $((((DATA & 0x20)) >> 5))
        echo VDD_SPD_EFGH: $((((DATA & 0x40)) >> 6))
        echo VDD_VPP_EFGH: $((((DATA & 0x80)) >> 7))

        let FPGA_REG=FPGA_REG+1
        DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
        echo VDD_VTT_EFGH : $((DATA & 0x01))
        echo VDD_MEM_EFGH : $((((DATA & 0x02)) >> 1))
        echo VDD_18_RUN-- : $((((DATA & 0x04)) >> 2))
        echo VDD_SOC_RUN- : $((((DATA & 0x08)) >> 3))
        echo VDD_CORE_RUN : $((((DATA & 0x10)) >> 4))
        let FPGA_REG=FPGA_REG+1
        let VDD_LOOP_CNT=VDD_LOOP_CNT+1
        done
    let SOCKET=SOCKET+1
    done

# Power State/Reset Data
FPGA_REG=24
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----Power state Information:
echo P0_SLP_S5_L--- : $((DATA & 0x01))
echo P0_SLP_S3_L--- : $((((DATA & 0x02)) >> 1))
echo ATX_PS_ON----- : $((((DATA & 0x04)) >> 2))
echo FPGA_5_DUAL_EN : $((((DATA & 0x08)) >> 3))

# Power Good information
FPGA_REG=25
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----Power Good Information:
echo VDD_33_DUAL_PG------- : $((DATA & 0x01))
echo FPGA_VDD_CORE_DUAL_PG : $((((DATA & 0x02)) >> 1))
echo MGMT_VDD_VPP_DUAL_PG- : $((((DATA & 0x04)) >> 2))
echo MGMT_VDD_MEM_DUAL_PG- : $((((DATA & 0x08)) >> 3))
echo MGMT_VDD_CORE_DUAL_PG : $((((DATA & 0x10)) >> 4))
echo ATX_PWR_OK----------- : $((((DATA & 0x20)) >> 5))

# Power and Reset Signals
FPGA_REG=26
PWRRST_REG_MAX=27
SOCKET=0
while [ $FPGA_REG -le $PWRRST_REG_MAX ]
    do
    DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
    echo ----------FPGAreg$FPGA_REG-----P$SOCKET Power and Reset Signals:
    echo RSMRST_L----------------- : $((DATA & 0x01))
    echo PWR_GOOD----------------- : $((((DATA & 0x02)) >> 1))
    echo PWRGD_OUT---------------- : $((((DATA & 0x04)) >> 2))
    echo FPGA_PWROK_RESET_BUF_EN_L : $((((DATA & 0x08)) >> 3))
    echo 33_PWROK----------------- : $((((DATA & 0x10)) >> 4))
    echo VDD_CORE_RUN_PWROK------- : $((((DATA & 0x20)) >> 5))
    echo VDD_SOC_RUN_PWROK-------- : $((((DATA & 0x40)) >> 6))
    echo 33_RESET_L--------------- : $((((DATA & 0x80)) >> 7))
    let FPGA_REG=FPGA_REG+1
    done

# Processor and power cable preset signals
FPGA_REG=28
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----Processor and power cable preset signals:
echo P0_PRESENT_L--------------------- : $((DATA & 0x01))
echo P0_VDD_MEM_ABCD_12_RUN_PLUG_PRSNT : $((((DATA & 0x02)) >> 1))
echo P0_VDD_MEM_EFGH_12_RUN_PLUG_PRSNT : $((((DATA & 0x04)) >> 2))
echo P0_VDD_12_RUN_PLUG_PRSNT--------- : $((((DATA & 0x08)) >> 3))
echo P1_PRESENT_L--------------------- : $((((DATA & 0x10)) >> 4))
echo P1_VDD_MEM_ABCD_12_RUN_PLUG_PRSNT : $((((DATA & 0x20)) >> 5))
echo P1_VDD_MEM_EFGH_12_RUN_PLUG_PRSNT : $((((DATA & 0x40)) >> 6))
echo P1_VDD_12_RUN_PLUG_PRSNT--------- : $((((DATA & 0x80)) >> 7))

# Board LEDs
FPGA_REG=29
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----LED States:
echo PWR_GOOD_LED--- : $((DATA & 0x01))
echo PWROK_LED------ : $((((DATA & 0x02)) >> 1))
echo RESET_LED_L---- : $((((DATA & 0x04)) >> 2))
echo P0_PROCHOT_LED- : $((((DATA & 0x08)) >> 3))
echo P1_PROCHOT_LED- : $((((DATA & 0x10)) >> 4))

# VR thermal errors
FPGA_REG=30
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----VR Thermal Errors:
echo P0_VDD_MEM_ABCD_SUS_VRHOT_L : $((DATA & 0x01))
echo P0_VDD_MEM_EFGH_SUS_VRHOT_L : $((((DATA & 0x02)) >> 1))
echo P0_VDD_SOC_RUN_VRHOT_L----- : $((((DATA & 0x04)) >> 2))
echo P0_VDD_CORE_RUN_VRHOT_L---- : $((((DATA & 0x08)) >> 3))
echo P1_VDD_MEM_ABCD_SUS_VRHOT_L : $((((DATA & 0x10)) >> 4))
echo P1_VDD_MEM_EFGH_SUS_VRHOT_L : $((((DATA & 0x20)) >> 5))
echo P1_VDD_SOC_RUN_VRHOT_L----- : $((((DATA & 0x40)) >> 6))
echo P1_VDD_CORE_RUN_VRHOT_L---- : $((((DATA & 0x80)) >> 7))

# Processor and board Thermal Errors
FPGA_REG=31
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----Processor and board Thermal Errors:
echo FPGA_P0_THERMTRIP_L : $((DATA & 0x01))
echo FPGA_P1_THERMTRIP_L : $((((DATA & 0x02)) >> 1))
echo SENSOR_THERM_L----- : $((((DATA & 0x04)) >> 2))
echo P0_PROCHOT_L------- : $((((DATA & 0x08)) >> 3))
echo P1_PROCHOT_L------- : $((((DATA & 0x10)) >> 4))

# AST2500 control Signals
FPGA_REG=32
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----AST2500 Control Signals:
echo MGMT_ASSERT_BMC_READY--- : $((DATA & 0x01))
echo MGMT_ASSERT_LOCAL_LOCK-- : $((((DATA & 0x02)) >> 1))
echo MGMT_ASSERT_PWR_BTN----- : $((((DATA & 0x04)) >> 2))
echo MGMT_ASSERT_RST_BTN----- : $((((DATA & 0x08)) >> 3))
echo MGMT_ASSERT_NMI_BTN----- : $((((DATA & 0x10)) >> 4))
echo MGMT_ASSERT_P0_PROCHOT-- : $((((DATA & 0x20)) >> 5))
echo MGMT_ASSERT_P1_PROCHOT-- : $((((DATA & 0x40)) >> 6))
echo MGMT_ASSERT_WARM_RST_BTN : $((((DATA & 0x80)) >> 7))

# FPGA processor control signals
FPGA_REG=33
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----FPGA processor Control Signals:
echo ASSERT_P0_PWROK_L-------- : $((DATA & 0x01))
echo ASSERT_P0_RESET_L-------- : $((((DATA & 0x02)) >> 1))
echo ASSERT_P0_PROCHOT_L------ : $((((DATA & 0x04)) >> 2))
echo MGMT_SYS_MON_P0_PROCHOT_L : $((((DATA & 0x08)) >> 3))
echo ASSERT_P1_PWROK_L-------- : $((((DATA & 0x10)) >> 4))
echo ASSERT_P1_RESET_L-------- : $((((DATA & 0x20)) >> 5))
echo ASSERT_P1_PROCHOT_L------ : $((((DATA & 0x40)) >> 6))
echo MGMT_SYS_MON_P1_PROCHOT_L : $((((DATA & 0x80)) >> 7))

# Buttons/Resets
FPGA_REG=34
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----Button and Reset Signals:
echo PWR_BTN_L----- : $((DATA & 0x01))
echo RST_BTN_L----- : $((((DATA & 0x02)) >> 1))
echo WARM_RST_BTN_L : $((((DATA & 0x04)) >> 2))
echo NMI_BTN_L----- : $((((DATA & 0x08)) >> 3))
echo FPGA_BTN_L---- : $((((DATA & 0x10)) >> 4))
echo P0_PWR_BTN_L-- : $((((DATA & 0x20)) >> 5))
echo P0_SYS_RESET_L : $((((DATA & 0x40)) >> 6))
echo P0_KBRST_L---- : $((((DATA & 0x80)) >> 7))

# Miscellaneous Block 1
FPGA_REG=35
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----Miscellaneous 35 Signals:
echo MGMT_AC_LOSS_L---------- : $((DATA & 0x01))
echo P0_NV_FORCE_SELF_REFRESH : $((((DATA & 0x02)) >> 1))
echo P1_NV_FORCE_SELF_REFRESH : $((((DATA & 0x04)) >> 2))
echo P0_LOCAL_SPI_ROM_SEL_L-- : $((((DATA & 0x08)) >> 3))
echo PCIE_SLOT4_HP_FON_L----- : $((((DATA & 0x10)) >> 4))
echo P0_NMI_SYNC_FLOOD_L----- : $((((DATA & 0x20)) >> 5))
echo FPGA_LPC_RST_L---------- : $((((DATA & 0x40)) >> 6))
echo MGMT_SMBUS_ALERT_L------ : $((((DATA & 0x80)) >> 7))

# Miscellaneous Block 2
FPGA_REG=36
SHUTDOWNERR=0
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----Miscellaneous 36 Signals:
echo physical_pg------------------- : $((DATA & 0x01))
echo shutdown_error---------------- : $((((DATA & 0x02)) >> 1))
SHUTDOWNERR=$((((DATA & 0x02)) >> 1))
echo P0_PRESENT_HDT---------------- : $((((DATA & 0x04)) >> 2))
echo P1_PRESENT_HDT---------------- : $((((DATA & 0x08)) >> 3))
echo DAP_EXT_P0_CORE_RUN_VOLTAGE_PG : $((((DATA & 0x10)) >> 4))
echo FPGA_BRD_ID------------------- : $((((DATA & 0x20)) >> 5))
echo FPGA_BRD_ID------------------- : $((((DATA & 0x40)) >> 6))
echo MGMT_FPGA_RSVD---------------- : $((((DATA & 0x80)) >> 7))

# Switch S1
FPGA_REG=37
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----Switch Bank S1:
if [ $((DATA & 0x01)) -eq 1 ] ; then
    echo "FPGA_SW1-1 - OFF - P0 PwrReg PU with Proc"
else
    echo "FPGA_SW1-1 - ON  - P0 PwrReg PU without Proc"
fi
if [ $((((DATA & 0x02)) >> 1)) -eq 1 ] ; then
    echo "FPGA_SW1-1 - OFF - P1 PwrReg PU with Proc"
else
    echo "FPGA_SW1-1 - ON  - P1 PwrReg PU without Proc"
fi
if [ $((((DATA & 0x04)) >> 2)) -eq 1 ] ; then
    echo "FPGA_SW1-3 - OFF - ATX Connectors Valid"
else
    echo "FPGA_SW1-3 - ON  - ATX Connectors Ignored"
fi
if [ $((((DATA & 0x08)) >> 3)) -eq 1 ] ; then
    echo "FPGA_SW1-4 - OFF - Wait for BMC Boot"
else
    echo "FPGA_SW1-4 - ON  - Do Not Wait for BMC Boot"
fi
if [ $((((DATA & 0x10)) >> 4)) -eq 1 ] ; then
    echo "FPGA_SW1-5 - OFF - MemPwrReg PU after ATX"
else
    echo "FPGA_SW1-5 - ON  - MemPwrReg PU before ATX"
fi
if [ $((((DATA & 0x20)) >> 5)) -eq 1 ] ; then
    echo "FPGA_SW1-6 - OFF - DAP CORE Reg Bypass DISABLED"
else
    echo "FPGA_SW1-6 - ON  - DAP CORE Reg Bypass ENABLED"
fi
if [ $((((DATA & 0x40)) >> 6)) -eq 1 ] ; then
    echo "FPGA_SW1-7 - OFF - Bypass P0 in HDT JTAG Chain DISABLED"
else
    echo "FPGA_SW1-7 - ON  - Bypass P0 in HDT JTAG Chain ENABLED"
fi
if [ $((((DATA & 0x80)) >> 7)) -eq 1 ] ; then
    echo "FPGA_SW1-8 - OFF - Bypass P1 in HDT JTAG Chain DISABLED"
else
    echo "FPGA_SW1-8 - ON  - Bypass P1 in HDT JTAG Chain ENABLED"
fi

# Switch S2
FPGA_REG=38
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
echo ----------FPGAreg$FPGA_REG-----Switch Bank S2:
if [ $((DATA & 0x01)) -eq 1 ] ; then
    echo "FPGA_SW2-1 - OFF - Boot from SPI ROM behind BMC"
else
    echo "FPGA_SW2-1 - ON  - Boot from P0 local SPI ROM"
fi
if [ $((((DATA & 0x02)) >> 1)) -eq 1 ] ; then
    echo "FPGA_SW2-2 - OFF - PCIe SLOT4 hot plug forced PwrON without driver"
else
    echo "FPGA_SW2-2 - ON  - PCIe SLOT4 hot plug NOT forced PwrON without driver"
fi
if [ $((((DATA & 0x04)) >> 2)) -eq 1 ] ; then
    echo "FPGA_SW2-3 - OFF - SMI testing DISABLED"
else
    echo "FPGA_SW2-3 - ON  - SMI testing ENABLED"
fi
if [ $((((DATA & 0x08)) >> 3)) -eq 1 ] ; then
    echo "FPGA_SW2-4 - OFF - PROCHOT testing DISABLED"
else
    echo "FPGA_SW2-4 - ON  - PROCHOT testing ENABLED"
fi
if [ $((((DATA & 0x10)) >> 4)) -eq 1 ] ; then
    echo "FPGA_SW2-5 - OFF - PwrCycle on post code C0 DISABLED"
else
    echo "FPGA_SW2-5 - ON  - PwrCycle on post code C0 ENABLED"
fi
if [ $((((DATA & 0x20)) >> 5)) -eq 1 ] ; then
    echo "FPGA_SW2-6 - OFF - PwrCycle Px DISABLED"
else
    echo "FPGA_SW2-6 - ON  - PwrCycle - Px Present - RESET_L | Px Not Present VR PwrGood"
fi
if [ $((((DATA & 0x40)) >> 6)) -eq 1 ] ; then
    echo "FPGA_SW2-7 - OFF - BMC IP Address display DISABLED"
else
    echo "FPGA_SW2-7 - ON  - BMC IP Address display ENABLED"
fi
if [ $((((DATA & 0x80)) >> 7)) -eq 1 ] ; then
    echo "FPGA_SW1-8 - OFF - FORCE_SELFREFRESH support diabled"
else
    echo "FPGA_SW1-8 - ON  - FORCE_SELFREFRESH support diabled"
fi

# Powerup Error Group
echo ------------------------Power and Thermal Error Group
if [ $SHUTDOWNERR = 0 ] ; then
    echo NO Shutdown Errors Detected
fi

FPGA_REG=40
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x0F)) != 0 ] ; then
    echo PU Error: PU1$((DATA & 0x0F))
    echo $DATA
fi

FPGA_REG=41
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x07)) != 0 ] ; then
    echo PU Error: PU2$((DATA & 0x07))
fi

FPGA_REG=42
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x0F)) != 0 ] ; then
    echo PU Error: PU1$((DATA & 0x0F))
fi

FPGA_REG=43
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x07)) != 0 ] ; then
    echo PU Error: PU4$((DATA & 0x07))
fi

FPGA_REG=44
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x03)) != 0 ] ; then
    echo PU Error: PU5$((DATA & 0x03))
fi

FPGA_REG=45
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x07)) != 0 ] ; then
    echo PU Error: PU6$((DATA & 0x07))
fi

# Powerdown Error Group
FPGA_REG=46
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x0F)) != 0 ] ; then
    echo PD Error: PD1$((DATA & 0x0F))
fi

FPGA_REG=47
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x07)) != 0 ] ; then
    echo PD Error: PD2$((DATA & 0x07))
fi

FPGA_REG=48
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x0F)) != 0 ] ; then
    echo PD Error: PD3$((DATA & 0x0F))
fi

FPGA_REG=49
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x07)) != 0 ] ; then
    echo PD Error: PD4$((DATA & 0x07))
fi

FPGA_REG=50
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x03)) != 0 ] ; then
    echo PD Error: PD5$((DATA & 0x03))
fi

FPGA_REG=51
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x03)) != 0 ] ; then
    echo PD Error: PD6$((DATA & 0x03))
fi

FPGA_REG=52
DATA=$(i2cget -y $I2CBUS $FPGAADDR $(printf "0x%x" $FPGA_REG))
if [ $((DATA & 0x0F)) != 0 ] ; then
    echo Thermal Error: H_0$((DATA & 0x0F))
fi
echo ------------- end of data -----------------
