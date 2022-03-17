#!/bin/bash

powerState=$(i2cget -f -y 0x2 0x4f 0xe0 b)

getGPISourceRegisters(){
    REG60=$(i2cget -f -y 0x2 0x4f 0x60 w)       # GPI Data Set
    REG61=$(i2cget -f -y 0x2 0x4f 0x61 w)       # GPI DATA Set #0
    REG62=$(i2cget -f -y 0x2 0x4f 0x62 w)       # GPI DATA Set #1
    REG63=$(i2cget -f -y 0x2 0x4f 0x63 w)       # GPI DATA Set #2
    REG64=$(i2cget -f -y 0x2 0x4f 0x64 w)       # GPI DATA Set #3
    DS0Pres=$((REG60 & 0x0100))
    DS1Pres=$((REG60 & 0x0200))
    DS2Pres=$((REG60 & 0x0400))
    DS3Pres=$((REG60 & 0x0800))
}

getErrorCount() {
    MemCE=$(i2cget -f -y 0x2 0x4f 0x90 w)
    MemUE=$(i2cget -f -y 0x2 0x4f 0x93 w)
    CoreCE=$(i2cget -f -y 0x2 0x4f 0x80 w)
    CoreUE=$(i2cget -f -y 0x2 0x4f 0x83 w)
    PCIeCE=$(i2cget -f -y 0x2 0x4f 0xc0 w)
    PCIeUE=$(i2cget -f -y 0x2 0x4f 0xc2 w)
    OtherCE=$(i2cget -f -y 0x2 0x4f 0xd0 w)
    OtherUE=$(i2cget -f -y 0x2 0x4f 0xd8 w)
}

getReg61Vals() {
    REG61_0800=$((REG61 & 0x0800))
    REG61_0800_VAL=$((REG61_0800 >> 8))
    REG61_1000=$((REG61 & 0x1000))
    REG61_1000_VAL=$((REG61_1000 >> 12))
}

getReg62Vals() {
    REG62_0100=$((REG62 & 0x0100))
    REG62_0100_VAL=$((REG62_0100 >> 8))
    REG62_0200=$((REG62 & 0x0200))
    REG62_0200_VAL=$((REG62_0200 >> 9))
    REG62_0400=$((REG62 & 0x0400))
    REG62_0400_VAL=$((REG62_0400 >> 10))
}

getReg63Vals() {
    REG63_0100=$((REG63 & 0x0100))
    REG63_0100_VAL=$((REG63_0100 >> 8))
}

getReg64Vals() {
    REG64_0100=$((REG64 & 0x0100))
    REG64_0100_VAL=$((REG64_0100 >> 8))
    REG64_0200=$((REG64 & 0x0200))
    REG64_0200_VAL=$((REG64_0200 >> 9))
    REG64_0800=$((REG64 & 0x0800))
    REG64_0800_VAL=$((REG64_0800 >> 11))
    REG64_2000=$((REG64 & 0x2000))
    REG64_2000_VAL=$((REG64_2000 >> 13))
    REG64_4000=$((REG64 & 0x4000))
    REG64_4000_VAL=$((REG64_4000 >> 14))
    REG64_8000=$((REG64 & 0x8000))
    REG64_8000_VAL=$((REG64_8000 >> 15))
    REG64_0001=$((REG64 & 0x0001))
    REG64_0001_VAL=$((REG64_0001))
}

getGPIStatusRegisters(){
    REG70=$(i2cget -f -y 0x2 0x4f 0x70 w)       # Core, DIMM, SLC, PCIe, and Other errors
    REG78=$(i2cget -f -y 0x2 0x4f 0x78 w)       # VRD Fault/Warning Error
    REG79=$(i2cget -f -y 0x2 0x4f 0x79 w)       # VRD Hot
    REG7A=$(i2cget -f -y 0x2 0x4f 0x7A w)       # DIMM Hot Error
    REG7B=$(i2cget -f -y 0x2 0x4f 0x7B w)       # Boot #1 Error
    REG7C=$(i2cget -f -y 0x2 0x4f 0x7C w)       # Boot #2 Error
    REG7D=$(i2cget -f -y 0x2 0x4f 0x7D w)       # Watchdog/Other Status
    REG7E=$(i2cget -f -y 0x2 0x4f 0x7E w)       # RAS internal error
}

getReg70Vals() {
    REG70_0100=$((REG70 & 0x0100))
    REG70_0100_VAL=$((REG70_0100 >> 8))
    REG70_0200=$((REG70 & 0x0200))
    REG70_0200_VAL=$((REG70_0200 >> 9))
    REG70_0400=$((REG70 & 0x0400))
    REG70_0400_VAL=$((REG70_0400 >> 10))
    REG70_0800=$((REG70 & 0x0800))
    REG70_0800_VAL=$((REG70_0800 >> 11))
    REG70_4000=$((REG70 & 0x4000))
    REG70_4000_VAL=$((REG70_4000 >> 14))
    REG70_8000=$((REG70 & 0x8000))
    REG70_8000_VAL=$((REG70_8000 >> 15))
    REG70_0001=$((REG70 & 0x0001))
    REG70_0001_VAL=$((REG70_0001))
    REG70_0002=$((REG70 & 0x0002))
    REG70_0002_VAL=$((REG70_0002 >> 1))
}

getReg78Vals() {
    REG78_0100=$((REG78 & 0x0100))
    REG78_0100_VAL=$((REG78_0100 >> 8))
    REG78_0200=$((REG78 & 0x0200))
    REG78_0200_VAL=$((REG78_0200 >> 9))
    REG78_0400=$((REG78 & 0x0400))
    REG78_0400_VAL=$((REG78_0400 >> 10))
    REG78_0800=$((REG78 & 0x0800))
    REG78_0800_VAL=$((REG78_0800 >> 11))
    REG78_1000=$((REG78 & 0x1000))
    REG78_1000_VAL=$((REG78_1000 >> 12))
    REG78_2000=$((REG78 & 0x2000))
    REG78_2000_VAL=$((REG78_2000 >> 13))
    REG78_4000=$((REG78 & 0x4000))
    REG78_4000_VAL=$((REG78_4000 >> 14))
    REG78_8000=$((REG78 & 0x8000))
    REG78_8000_VAL=$((REG78_8000 >> 15))
    REG78_0001=$((REG78 & 0x0001))
    REG78_0001_VAL=$((REG78_0001))
    REG78_0002=$((REG78 & 0x0002))
    REG78_0002_VAL=$((REG78_0002 >> 1))
    REG78_0004=$((REG78 & 0x0004))
    REG78_0004_VAL=$((REG78_0004 >> 2))
    REG78_0008=$((REG78 & 0x0008))
    REG78_0008_VAL=$((REG78_0008 >> 3))
}

getReg79Vals() {
    REG79_0100=$((REG79 & 0x0100))
    REG79_0100_VAL=$((REG79_0100 >> 8))
    REG79_1000=$((REG79 & 0x1000))
    REG79_1000_VAL=$((REG79_1000 >> 12))
    REG79_2000=$((REG79 & 0x2000))
    REG79_2000_VAL=$((REG79_2000 >> 13))
    REG79_4000=$((REG79 & 0x4000))
    REG79_4000_VAL=$((REG79_4000 >> 14))
    REG79_0001=$((REG79 & 0x0001))
    REG79_0001_VAL=$((REG79_0001))
    REG79_0002=$((REG79 & 0x0002))
    REG79_0002_VAL=$((REG79_0002 >> 1))
    REG79_0004=$((REG79 & 0x0004))
    REG79_0004_VAL=$((REG79_0004 >> 2))
    REG79_0008=$((REG79 & 0x0008))
    REG79_0008_VAL=$((REG79_0008 >> 3))
}

getReg7AVals() {
    REG7A_0100=$((REG7A & 0x0100))
    REG7A_0100_VAL=$((REG7A_0100 >> 8))
    REG7A_0200=$((REG7A & 0x0200))
    REG7A_0200_VAL=$((REG7A_0200 >> 9))
    REG7A_0400=$((REG7A & 0x0400))
    REG7A_0400_VAL=$((REG7A_0400 >> 10))
    REG7A_0800=$((REG7A & 0x0800))
    REG7A_0800_VAL=$((REG7A_0800 >> 11))
    REG7A_1000=$((REG7A & 0x1000))
    REG7A_1000_VAL=$((REG7A_1000 >> 12))
    REG7A_2000=$((REG7A & 0x2000))
    REG7A_2000_VAL=$((REG7A_2000 >> 13))
    REG7A_4000=$((REG7A & 0x4000))
    REG7A_4000_VAL=$((REG7A_4000 >> 14))
    REG7A_8000=$((REG7A & 0x8000))
    REG7A_8000_VAL=$((REG7A_8000 >> 15))
}

getReg7DVals() {
    REG7D_0100=$((REG7D & 0x0100))
    REG7D_0100_VAL=$((REG7D_0100 >> 8))
    REG7D_0200=$((REG7D & 0x0200))
    REG7D_0200_VAL=$((REG7D_0200 >> 9))
    REG7D_0400=$((REG7D & 0x0400))
    REG7D_0400_VAL=$((REG7D_0400 >> 10))
}

getReg7EVals() {
    REG7E_0100=$((REG7E & 0x0100))
    REG7E_0100_VAL=$((REG7E_0100 >> 8))
    REG7E_0200=$((REG7E & 0x0200))
    REG7E_0200_VAL=$((REG7E_0200 >> 9))
}

getGPIMaskRegisters(){
    REG50=$(i2cget -f -y 0x2 0x4f 0x50 w)       # GPI Control #0
    REG51=$(i2cget -f -y 0x2 0x4f 0x51 w)       # GPI Control #1
    REG52=$(i2cget -f -y 0x2 0x4f 0x52 w)       # GPI Control #2
    REG53=$(i2cget -f -y 0x2 0x4f 0x53 w)       # GPI Control #3
    REG54=$(i2cget -f -y 0x2 0x4f 0x54 w)       # GPI CE/UE Mask
}

getReg50Vals() {
    REG50_0400=$((REG50 & 0x0400))
    REG50_0400_VAL=$((REG50_0400 >> 10))
    REG50_0800=$((REG50 & 0x0800))
    REG50_0800_VAL=$((REG50_0800 >> 11))
}

getReg51Vals() {
    REG51_0100=$((REG51 & 0x0100))
    REG51_0100_VAL=$((REG51_0100 >> 8))
    REG51_0200=$((REG51 & 0x0200))
    REG51_0200_VAL=$((REG51_0200 >> 9))
    REG51_0400=$((REG51 & 0x0400))
    REG51_0400_VAL=$((REG51_0400 >> 10))
}

getReg52Vals() {
    REG52_0100=$((REG52 & 0x0100))
    REG52_0100_VAL=$((REG52_0100 >> 8))
}

getReg53Vals() {
    REG53_0100=$((REG53 & 0x0100))
    REG53_0100_VAL=$((REG53_0100 >> 8))
    REG53_0200=$((REG53 & 0x0200))
    REG53_0200_VAL=$((REG53_0200 >> 9))
    REG53_0800=$((REG53 & 0x0800))
    REG53_0800_VAL=$((REG53_0800 >> 11))
    REG53_2000=$((REG53 & 0x2000))
    REG53_2000_VAL=$((REG53_2000 >> 13))
    REG53_4000=$((REG53 & 0x4000))
    REG53_4000_VAL=$((REG53_4000 >> 14))
    REG53_8000=$((REG53 & 0x8000))
    REG53_8000_VAL=$((REG53_8000 >> 15))
    REG53_0001=$((REG53 & 0x0001))
    REG53_0001_VAL=$((REG53_0001))
}

getReg54Vals() {
    REG54_0100=$((REG54 & 0x0100))
    REG54_0100_VAL=$((REG54_0100 >> 8))
    REG54_0200=$((REG54 & 0x0200))
    REG54_0200_VAL=$((REG54_0200 >> 9))
    REG54_0001=$((REG54 & 0x0001))
    REG54_0001_VAL=$((REG54_0001))
    REG54_0002=$((REG54 & 0x0002))
    REG54_0002_VAL=$((REG54_0002 >> 1))
    REG54_0004=$((REG54 & 0x0004))
    REG54_0004_VAL=$((REG54_0004 >> 2))
    REG54_0008=$((REG54 & 0x0008))
    REG54_0008_VAL=$((REG54_0008 >> 3))
    REG54_0010=$((REG54 & 0x0010))
    REG54_0010_VAL=$((REG54_0010 >> 4))
    REG54_0020=$((REG54 & 0x0020))
    REG54_0020_VAL=$((REG54_0020 >> 5))
}



if [ -z "${powerState}" ]
 then
        echo "System is currently Powered off S6"
else
        echo "System is currently in ${powerState} "

        # Get Error Count
        getErrorCount
        echo "  "
        echo " Error Count: "
        echo " Memory   Errors: Correctable $((MemCE >> 8)) Uncorrectable $((MemUE >> 8)) "
        echo " Core     Errors: Correctable $((CoreCE >> 8)) Uncorrectable $((CoreUE >> 8))"
        echo " PCIe     Errors: Correctable $((PCIeCE >> 8)) Uncorrectable $((PCIeUE >> 8)) "
        echo " Other    Errors: Correctable $((OtherCE >> 8)) Uncorrectable $((OtherUE >> 8)) "

        # GPI Source Registers
        getGPISourceRegisters
        echo "  "
        echo " GPI Source Registers: "
        echo " GPI Data Set #0 Present: $((DS0Pres >> 8)) "
        echo " GPI Data Set #1 Present: $((DS1Pres >> 9)) "
        echo " GPI Data Set #2 Present: $((DS2Pres >> 10)) "
        echo " GPI Data Set #3 Present: $((DS3Pres >> 11)) "

        # REG61 Data Set#0
        getReg61Vals
        echo "  "
        echo " GPI Data Set#0: "
        if [[ "$REG61_0800_VAL" != 0 ]]; then
                echo " Platform Booting "
        fi
        if [[ "$REG61_1000_VAL" != 0 ]]; then
                echo " Critical Stop "
        fi

        # REG62 Data Set#1
        getReg62Vals
        echo "  "
        echo " GPI Data Set#1: "
        if [[ "$REG62_0100_VAL" != 0 ]]; then
                echo " SoC VR HOT/Warn/Fault "
        fi
        if [[ "$REG62_0200_VAL" != 0 ]]; then
                echo " Core VR HOT/Warn/Fault "
        fi
        if [[ "$REG62_0400_VAL" != 0 ]]; then
                echo " DIMM HOT/Warn/Fault "
        fi

        # REG63 Data Set#2
        getReg63Vals
        echo "  "
        echo " GPI Data Set#2: "
        if [[ "$REG63_0100_VAL" != 0 ]]; then
                echo " DIMM HOT "
        fi

        # REG64 Data Set#3
        getReg64Vals
        echo "  "
        echo " GPI Data Set#3: "
        if [[ "$REG64_0100_VAL" != 0 ]]; then
                echo " Core Errors "
        fi
        if [[ "$REG64_0200_VAL" != 0 ]]; then
                echo " Memory Errors "
        fi
        if [[ "$REG64_0800_VAL" != 0 ]]; then
                echo " PCIe Errors "
        fi
        if [[ "$REG64_2000_VAL" != 0 ]]; then
                echo " Other Errors "
        fi
        if [[ "$REG64_4000_VAL" != 0 ]]; then
                echo " ACPI State Change "
        fi
        if [[ "$REG64_8000_VAL" != 0 ]]; then
                echo " Boot Errors "
        fi
        if [[ "$REG64_0001_VAL" != 0 ]]; then
                echo " RAS Internal Error "
    fi

        # GPI Status Regs
        getGPIStatusRegisters
        echo "  "
        echo " GPI Status Regs "

        getReg70Vals
        echo "  "
        echo " Core, DIMM, SLC, PCIe, and Other errors: "
        if [[ "$REG70_0100_VAL" != 0 ]]; then
                echo " Core CE Error "
        fi
        if [[ "$REG70_0200_VAL" != 0 ]]; then
                echo " Core UE Error "
        fi
        if [[ "$REG70_0400_VAL" != 0 ]]; then
                echo " DIMM CE Error "
        fi
        if [[ "$REG70_0800_VAL" != 0 ]]; then
                echo " DIMM UE Error "
        fi
        if [[ "$REG70_4000_VAL" != 0 ]]; then
                echo " PCIe CE Error "
        fi
        if [[ "$REG70_8000_VAL" != 0 ]]; then
                echo " PCIe UE Error "
        fi
        if [[ "$REG70_0001_VAL" != 0 ]]; then
                echo " Other CE Error "
        fi
        if [[ "$REG70_0002_VAL" != 0 ]]; then
                echo " Other UE Error "
        fi

        getReg78Vals
        echo "  "
        echo " VRD Fault/Warning Error: "
        if [[ "$REG78_0100_VAL" != 0 ]]; then
                echo " SoC VRD fault/warning "
        fi
        if [[ "$REG78_0200_VAL" != 0 ]]; then
                echo " Core VRD1 fault/warning "
        fi
        if [[ "$REG78_0400_VAL" != 0 ]]; then
                echo " Core VRD2 fault/warning "
        fi
        if [[ "$REG78_0800_VAL" != 0 ]]; then
                echo " Core VRD3 fault/warning "
        fi
        if [[ "$REG78_1000_VAL" != 0 ]]; then
                echo " DIMM VRD1 fault/warning "
        fi
        if [[ "$REG78_2000_VAL" != 0 ]]; then
                echo " DIMM VRD2 fault/warning "
        fi
        if [[ "$REG78_4000_VAL" != 0 ]]; then
                echo " DIMM VRD3 fault/warning "
        fi
        if [[ "$REG78_8000_VAL" != 0 ]]; then
                echo " DIMM VRD3 fault/warning "
        fi
        if [[ "$REG78_0001_VAL" != 0 ]]; then
                echo " DIMM fault/warning "
        fi
        if [[ "$REG78_0002_VAL" != 0 ]]; then
                echo " DIMM fault/warning "
        fi
        if [[ "$REG78_0004_VAL" != 0 ]]; then
                echo " DIMM fault/warning "
        fi
        if [[ "$REG78_0008_VAL" != 0 ]]; then
                echo " DIMM fault/warning "
        fi

        getReg79Vals
        echo "  "
        echo " VRD Hot: "
        if [[ "$REG79_0100_VAL" != 0 ]]; then
                echo " SoC VRD is HOT "
        fi
        if [[ "$REG79_1000_VAL" != 0 ]]; then
                echo " Core VRD1 is HOT "
        fi
        if [[ "$REG79_2000_VAL" != 0 ]]; then
                echo " Core VRD2 is HOT "
        fi
        if [[ "$REG79_4000_VAL" != 0 ]]; then
                echo " Core VRD3 is HOT "
        fi
        if [[ "$REG79_0001_VAL" != 0 ]]; then
                echo " DIMM VRD1 is HOT "
        fi
        if [[ "$REG79_0002_VAL" != 0 ]]; then
                echo " DIMM VRD2 is HOT "
        fi
        if [[ "$REG79_0004_VAL" != 0 ]]; then
                echo " DIMM VRD3 is HOT "
        fi
        if [[ "$REG79_0008_VAL" != 0 ]]; then
                echo " DIMM VRD4 is HOT "
        fi

        getReg7AVals
        echo "  "
        echo " DIMM Hot Error: "
        if [[ "$REG7A_0100_VAL" != 0 ]]; then
                echo " DIMM channel 0 is HOT "
        fi
        if [[ "$REG7A_0200_VAL" != 0 ]]; then
                echo " DIMM channel 1 is HOT "
        fi
        if [[ "$REG7A_0400_VAL" != 0 ]]; then
                echo " DIMM channel 2 is HOT "
        fi
        if [[ "$REG7A_0800_VAL" != 0 ]]; then
                echo " DIMM channel 3 is HOT "
        fi
        if [[ "$REG7A_1000_VAL" != 0 ]]; then
                echo " DIMM channel 4 is HOT "
        fi
        if [[ "$REG7A_2000_VAL" != 0 ]]; then
                echo " DIMM channel 5 is HOT "
        fi
        if [[ "$REG7A_4000_VAL" != 0 ]]; then
                echo " DIMM channel 6 is HOT "
        fi
        if [[ "$REG7A_8000_VAL" != 0 ]]; then
                echo " DIMM channel 7 is HOT "
        fi

        echo "  "
        echo " Boot #1 Error: $((REG7B >> 8)) "
        echo " Boot #2 Error: $((REG7C >> 8)) "


        getReg7DVals
        echo "  "
        echo " Watchdog/Other Status: "
        if [[ "$REG7D_0100_VAL" != 0 ]]; then
                echo " Non-secure WDT expired "
        fi
        if [[ "$REG7D_0200_VAL" != 0 ]]; then
                echo " Secure WDT expired "
        fi
        if [[ "$REG7D_0400_VAL" != 0 ]]; then
                echo " Firmware WDT expired "
        fi

        getReg7EVals
        echo "  "
        echo " RAS internal error: "
        if [[ "$REG7E_0100_VAL" != 0 ]]; then
                echo " Error from SMpro "
        fi
        if [[ "$REG7E_0200_VAL" != 0 ]]; then
                echo " Error from PMpro "
        fi

        # GPI Mask Regs
        getGPIMaskRegisters
        echo "  "
        echo " GPI Mask Regs "

        getReg50Vals
        echo "  "
        echo " GPI Control #0: "

        if [[ "$REG50_0400_VAL" != 0 ]]; then
                echo " Platform Booting "
        fi
        if [[ "$REG50_0800_VAL" != 0 ]]; then
                echo " Critical Stop "
        fi

        getReg51Vals
        echo "  "
        echo " GPI Control #1: "

        if [[ "$REG51_0100_VAL" != 0 ]]; then
                echo " SoC VR HOT/Warn/Fault "
        fi
        if [[ "$REG51_0200_VAL" != 0 ]]; then
                echo " Core VR HOT/Warn/Fault "
        fi
        if [[ "$REG51_0400_VAL" != 0 ]]; then
                echo " DIMM VRD HOT/Warn/Fault "
        fi

        getReg52Vals
        echo "  "
        echo " GPI Control #2: "

        if [[ "$REG52_0100_VAL" != 0 ]]; then
                echo " DIMM HOT "
        fi

        getReg53Vals
        echo "  "
        echo " GPI Control #3: "
        if [[ "$REG53_0100_VAL" != 0 ]]; then
                echo " Core Errors "
        fi
        if [[ "$REG53_0200_VAL" != 0 ]]; then
                echo " Memory Errors "
        fi
        if [[ "$REG53_0800_VAL" != 0 ]]; then
                echo " PCIe Errors "
        fi
        if [[ "$REG53_2000_VAL" != 0 ]]; then
                echo " Other SoC Errors "
        fi
        if [[ "$REG53_4000_VAL" != 0 ]]; then
                echo " ACPI State Change "
        fi
        if [[ "$REG53_8000_VAL" != 0 ]]; then
                echo " Boot Errors "
        fi
        if [[ "$REG53_0001_VAL" != 0 ]]; then
                echo " RAS Internal Error "
        fi

        getReg54Vals
        echo "  "
        echo " GPI CE/UE Mask: "
        if [[ "$REG54_0100_VAL" != 0 ]]; then
                echo " Core CE "
        fi
        if [[ "$REG54_0200_VAL" != 0 ]]; then
                echo " Core UE "
        fi
        if [[ "$REG54_0001_VAL" != 0 ]]; then
                echo " DIMM CE "
        fi
        if [[ "$REG54_0002_VAL" != 0 ]]; then
                echo " DIMM UE "
        fi
        if [[ "$REG54_0004_VAL" != 0 ]]; then
                echo " PCIe CE "
        fi
        if [[ "$REG54_0008_VAL" != 0 ]]; then
                echo " PCIe UE "
        fi
        if [[ "$REG54_0010_VAL" != 0 ]]; then
                echo " Other CE "
        fi
        if [[ "$REG54_0020_VAL" != 0 ]]; then
                echo " Other UE "
        fi

fi
