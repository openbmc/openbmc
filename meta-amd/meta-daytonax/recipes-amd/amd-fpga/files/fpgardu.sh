#!/bin/bash
echo
echo "-----FPGA Daytona<x> CRB Register Dump Utility"
echo

I2CBUS=2
FPGAADDR=0x41

FPGA_REG=1
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo M_ABCD_EVENT_R_BUF_N----- : $(((DATA & 0x80) >> 7))
echo M_EFGH_EVENT_R_BUF_N----- : $(((DATA & 0x40) >> 6))
echo M_IJKL_EVENT_R_BUF_N----- : $(((DATA & 0x20) >> 5))
echo M_MNOP_EVENT_R_BUF_N----- : $(((DATA & 0x10) >> 4))

FPGA_REG=2
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo BMC_NVDIMM_PRSNT_R_N----- : $(((DATA & 0x80) >> 7))
echo FM_ADR_TRIGGER_CPU_BUFF_N : $(((DATA & 0x40) >> 6))
echo FM_BMC_ONCTL_N----------- : $(((DATA & 0x20) >> 5))
echo FM_NVDIMM_EVENT_N-------- : $(((DATA & 0x10) >> 4))
echo P0_FORCE_SELFREFRESH----- : $(((DATA & 0x08) >> 3))
echo P0_NV_SAVE--------------- : $(((DATA & 0x04) >> 2))
echo P1_FORCE_SELFREFRESH----- : $(((DATA & 0x02) >> 1))
echo P1_NV_SAVE--------------- : $((DATA & 0x01))

FPGA_REG=3
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo CPLD_PWR_BTN_N----------- : $(((DATA & 0x80) >> 7))
echo FM_DEBUG_RST_BTN_N------- : $(((DATA & 0x40) >> 6))
echo P0_PWR_BTN_N------------- : $(((DATA & 0x20) >> 5))
echo PWRBTN_CPLD_IN_N--------- : $(((DATA & 0x10) >> 4))
echo FM_PLD_DEBUG_MODE_N------ : $(((DATA & 0x08) >> 3))
echo FM_PLD_DEBUG0------------ : $(((DATA & 0x04) >> 2))
echo FM_PLD_DEBUG1------------ : $(((DATA & 0x02) >> 1))
echo FM_PLD_DEBUG0------------ : $((DATA & 0x01))

FPGA_REG=4
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo FM_PLD_DEBUG3------------ : $(((DATA & 0x80) >> 7))
echo FM_PLD_DEBUG4------------ : $(((DATA & 0x40) >> 6))
echo FM_PLD_DEBUG5------------ : $(((DATA & 0x20) >> 5))
echo FM_PLD_DEBUG6------------ : $(((DATA & 0x10) >> 4))
echo FM_PLD_DEBUG7------------ : $(((DATA & 0x08) >> 3))
echo BP_SIG_CABLE_PRES_R_N---- : $(((DATA & 0x04) >> 2))
echo CPLD_P0_THERMTRIP_N------ : $(((DATA & 0x02) >> 1))
echo CPLD_P1_THERMTRIP_N------ : $((DATA & 0x01))

FPGA_REG=5
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo FM_BMC_CPLD_GPO---------- : $(((DATA & 0x80) >> 7))
echo FM_BMC_READY_N----------- : $(((DATA & 0x40) >> 6))
echo FM_CPLD_BMC_PWRDN_N------ : $(((DATA & 0x20) >> 5))
echo LED_PWR_AMBER_R---------- : $(((DATA & 0x10) >> 4))
echo LED_PWR_GRN_R------------ : $(((DATA & 0x08) >> 3))
echo P0_CORETYPE-------------- : $(((DATA & 0x04) >> 2))
echo P0_CPU_PRESENT_HDT------- : $(((DATA & 0x02) >> 1))
echo P0_CPU_PRESENT_N--------- : $((DATA & 0x01))

FPGA_REG=6
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo P0_NMI_SYNC_FLOOD_N------ : $(((DATA & 0x80) >> 7))
echo P0_PWROK_RST_BUF_EN_N---- : $(((DATA & 0x40) >> 6))
echo P0_SP3R1----------------- : $(((DATA & 0x20) >> 5))
echo P0_SP3R2_R--------------- : $(((DATA & 0x10) >> 4))
echo P1_CORETYPE-------------- : $(((DATA & 0x08) >> 3))
echo P1_CPU_PRESENT_HDT------- : $(((DATA & 0x04) >> 2))
echo P1_CPU_PRESENT_N--------- : $(((DATA & 0x02) >> 1))
echo P1_PWROK_RST_BUF_EN_N---- : $((DATA & 0x01))

FPGA_REG=7
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo P1_SP3R1----------------- : $(((DATA & 0x80) >> 7))
echo P1_SP3R2_R--------------- : $(((DATA & 0x40) >> 6))
echo PSU1_BLADE_EN_R_N-------- : $(((DATA & 0x20) >> 5))
echo SLOT1_CLKREQ_N----------- : $(((DATA & 0x10) >> 4))
echo SLOT1_PRSNT_N------------ : $(((DATA & 0x08) >> 3))
echo SLOT2_CLKREQ_N----------- : $(((DATA & 0x04) >> 2))
echo SLOT2_PRSNT_N------------ : $(((DATA & 0x02) >> 1))
echo SMB_M2_S0_ALERT_N-------- : $((DATA & 0x01))

FPGA_REG=8
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo SMB_M2_S1_ALERT_N-------- : $(((DATA & 0x80) >> 7))
echo FM_BMC_READ_SPD_TEMP----- : $(((DATA & 0x40) >> 6))
echo PWR_ALL_ON_N------------- : $(((DATA & 0x20) >> 5))
echo I2C_SELECT_CPLD---------- : $(((DATA & 0x10) >> 4))
echo CPLD_PWRBRK_N------------ : $(((DATA & 0x08) >> 3))
echo FM_PWRBRK_N-------------- : $(((DATA & 0x04) >> 2))
echo PSU1_THROTTLE_N---------- : $(((DATA & 0x02) >> 1))
echo PSU2_ALERT_EN_N---------- : $((DATA & 0x01))

FPGA_REG=9
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo PSU2_ALERT_N------------- : $(((DATA & 0x80) >> 7))
echo RM_THROTTLE_EN_N----- ----: $(((DATA & 0x40) >> 6))
echo FM_P1V8_AUX_P0_EN-------- : $(((DATA & 0x20) >> 5))
echo FM_P1V8_AUX_P1_EN-------- : $(((DATA & 0x10) >> 4))
echo FM_P1V8_P0_EN------------ : $(((DATA & 0x08) >> 3))
echo FM_P1V8_P1_EN------------ : $(((DATA & 0x04) >> 2))
echo FM_P5V_EN---------------- : $(((DATA & 0x02) >> 1))
echo FM_PS_P12V_EN------------ : $((DATA & 0x01))

FPGA_REG=10
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo FM_PS_P12V_FAN_EN-------- : $(((DATA & 0x80) >> 7))
echo FM_PVDDIO_ABCD_EN-------- : $(((DATA & 0x40) >> 6))
echo FM_PVDDIO_EFGH_EN-------- : $(((DATA & 0x20) >> 5))
echo FM_PVDDIO_IJKL_EN-------- : $(((DATA & 0x10) >> 4))
echo FM_PVDDIO_MNOP_EN-------- : $(((DATA & 0x08) >> 3))
echo FM_PVPP_ABCD_EN---------- : $(((DATA & 0x04) >> 2))
echo FM_PVPP_EFGH_EN---------- : $(((DATA & 0x02) >> 1))
echo FM_PVPP_IJKL_EN---------- : $((DATA & 0x01))

FPGA_REG=11
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo FM_PVPP_MNOP_EN---------- : $(((DATA & 0x80) >> 7))
echo P0_VDDCR_CPU_EN1--------- : $(((DATA & 0x40) >> 6))
echo P0_VDDCR_CPU_PWROK_R----- : $(((DATA & 0x20) >> 5))
echo P0_VDDCR_SOC_AUX_EN------ : $(((DATA & 0x10) >> 4))
echo P0_VDDCR_SOC_EN1--------- : $(((DATA & 0x08) >> 3))
echo P0_VDDCR_SOC_PWROK_R----- : $(((DATA & 0x04) >> 2))
echo P1_VDDCR_CPU_EN1--------- : $(((DATA & 0x02) >> 1))
echo P1_VDDCR_CPU_PWROK_R----- : $((DATA & 0x01))

FPGA_REG=12
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo P1_VDDCR_SOC_AUX_EN------ : $(((DATA & 0x80) >> 7))
echo P1_VDDCR_SOC_EN1--------- : $(((DATA & 0x40) >> 6))
echo P1_VDDCR_SOC_PWROK_R----- : $(((DATA & 0x20) >> 5))
echo PVTT_ABCD_EN------------- : $(((DATA & 0x10) >> 4))
echo PVTT_EFGH_EN------------- : $(((DATA & 0x08) >> 3))
echo PVTT_IJKL_EN------------- : $(((DATA & 0x04) >> 2))
echo PVTT_MNOP_EN------------- : $(((DATA & 0x02) >> 1))
echo VR_P3V3_EN_N------------- : $((DATA & 0x01))

FPGA_REG=13
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo ASSERT_P0_PWROK_L------- : $(((DATA & 0x80) >> 7))
echo ASSERT_P1_PWROK_L------- : $(((DATA & 0x40) >> 6))
echo HDT_HDR_PWROK----------- : $(((DATA & 0x20) >> 5))
echo P0_33_PWROK------------- : $(((DATA & 0x10) >> 4))
echo P0_PWR_GOOD------------- : $(((DATA & 0x08) >> 3))
echo P0_PWRGD_OUT------------ : $(((DATA & 0x04) >> 2))
echo P0_VDDCR_CPU_PG1-------- : $(((DATA & 0x02) >> 1))
echo P0_VDDCR_SOC_PG1-------- : $((DATA & 0x01))

FPGA_REG=14
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo P1_33_PWROK------------- : $(((DATA & 0x80) >> 7))
echo P1_PWR_GOOD------------- : $(((DATA & 0x40) >> 6))
echo P1_PWRGD_OUT------------ : $(((DATA & 0x20) >> 5))
echo P1_VDDCR_CPU_PG1-------- : $(((DATA & 0x10) >> 4))
echo P1_VDDCR_SOC_PG1-------- : $(((DATA & 0x08) >> 3))
echo P3V3_AUX_PWRGD---------- : $(((DATA & 0x04) >> 2))
echo PWRGD_BMC_ALL----------- : $(((DATA & 0x02) >> 1))
echo PWRGD_P0_VDDCR_SOC_AUX-- : $((DATA & 0x01))

FPGA_REG=15
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo PWRGD_P1_VDDCR_SOC_AUX-- : $(((DATA & 0x80) >> 7))
echo PWRGD_P12V-------------- : $(((DATA & 0x40) >> 6))
echo PWRGD_P12V_FAN_R-------- : $(((DATA & 0x20) >> 5))
echo PWRGD_P1V8_AUX_P0------- : $(((DATA & 0x10) >> 4))
echo PWRGD_P1V8_AUX_P1------- : $(((DATA & 0x08) >> 3))
echo PWRGD_P1V8_P0----------- : $(((DATA & 0x04) >> 2))
echo PWRGD_P1V8_P1----------- : $(((DATA & 0x02) >> 1))
echo PWRGD_P3V3_R3----------- : $((DATA & 0x01))

FPGA_REG=16
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo PWRGD_P5V_CPLD_R-------- : $(((DATA & 0x80) >> 7))
echo PWRGD_PVDDIO_ABCD------- : $(((DATA & 0x40) >> 6))
echo PWRGD_PVDDIO_EFGH------- : $(((DATA & 0x20) >> 5))
echo PWRGD_PVDDIO_IJKL------- : $(((DATA & 0x10) >> 4))
echo PWRGD_PVDDIO_MNOP------- : $(((DATA & 0x08) >> 3))
echo PWRGD_PVPP_ABCD--------- : $(((DATA & 0x04) >> 2))
echo PWRGD_PVPP_EFGH--------- : $(((DATA & 0x02) >> 1))
echo PWRGD_PVPP_IJKL--------- : $((DATA & 0x01))

FPGA_REG=17
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo PWRGD_PVPP_MNOP--------- : $(((DATA & 0x80) >> 7))
echo PWRGD_PVTT_ABCD--------- : $(((DATA & 0x40) >> 6))
echo PWRGD_PVTT_EFGH--------- : $(((DATA & 0x20) >> 5))
echo PWRGD_PVTT_IJKL--------- : $(((DATA & 0x10) >> 4))
echo PWRGD_PVTT_MNOP--------- : $(((DATA & 0x08) >> 3))
echo PWRGD_SYS_BMC_PWROK----- : $(((DATA & 0x04) >> 2))
echo P0_SLP_S3_N------------- : $(((DATA & 0x02) >> 1))
echo P0_SLP_S5_N------------- : $((DATA & 0x01))

FPGA_REG=18
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo PSU2_PS_ON_N----------- : $(((DATA & 0x80) >> 7))
echo BMC_PWRCAP_N----------- : $(((DATA & 0x40) >> 6))
echo CPLD_FPH_ALERT_R_N----- : $(((DATA & 0x20) >> 5))
echo FAST_PROCHOT_R_N------- : $(((DATA & 0x10) >> 4))
echo FM_THROTTLE_IN_N------- : $(((DATA & 0x08) >> 3))
echo HSC_GPIO0_PLD_N-------- : $(((DATA & 0x04) >> 2))
echo HSC_GPIO1_PLD_N-------- : $(((DATA & 0x02) >> 1))
echo P0_BMC_PROCHOT_N------- : $((DATA & 0x01))

FPGA_REG=19
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo P1_BMC_PROCHOT_N------- : $(((DATA & 0x80) >> 7))
echo PMB_ALERT_SW_N--------- : $(((DATA & 0x40) >> 6))
echo RM_THROTTLE_SW_N------- : $(((DATA & 0x20) >> 5))
echo RST_PLTRST_DLY--------- : $(((DATA & 0x10) >> 4))
echo UV_ALERT_R_N----------- : $(((DATA & 0x08) >> 3))
echo ASSERT_P0_RESET-------- : $(((DATA & 0x04) >> 2))
echo ASSERT_P1_RESET-------- : $(((DATA & 0x02) >> 1))
echo HDT_HDR_RESET_L-------- : $((DATA & 0x01))

FPGA_REG=20
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo RST_CPLD_BMC_R_N------- : $(((DATA & 0x80) >> 7))
echo RST_CPU_1V8_N---------- : $(((DATA & 0x40) >> 6))
echo RST_KBRST_P0_N--------- : $(((DATA & 0x20) >> 5))
echo RST_P0_3V3_N----------- : $(((DATA & 0x10) >> 4))
echo RST_P0_PE0_N----------- : $(((DATA & 0x08) >> 3))
echo RST_P0_PE1_N----------- : $(((DATA & 0x04) >> 2))
echo RST_P0_PE2_N----------- : $(((DATA & 0x02) >> 1))
echo RST_P0_PE3_N,---------- : $((DATA & 0x01))

FPGA_REG=21
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo RST_P0_SASHD_0_R_N----- : $(((DATA & 0x80) >> 7))
echo RST_P0_SASHD_1_R_N----- : $(((DATA & 0x40) >> 6))
echo RST_P1_3V3_N----------- : $(((DATA & 0x20) >> 5))
echo RST_P1_OCU1_R_N-------- : $(((DATA & 0x10) >> 4))
echo RST_P1_PE0_N----------- : $(((DATA & 0x08) >> 3))
echo RST_P1_PE1_N----------- : $(((DATA & 0x04) >> 2))
echo RST_P1_PE2_N----------- : $(((DATA & 0x02) >> 1))
echo RST_P1_PE3_N,---------- : $((DATA & 0x01))

FPGA_REG=22
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo RST_BMC_RSTBTN_OUT_N_CPLD : $(((DATA & 0x80) >> 7))
echo RST_PE_NVME0_N----------- : $(((DATA & 0x40) >> 6))
echo RST_PE_NVME1_N----------- : $(((DATA & 0x20) >> 5))
echo RST_PE_NVME2_N----------- : $(((DATA & 0x10) >> 4))
echo RST_PE_NVME3_N----------- : $(((DATA & 0x08) >> 3))
echo RST_PE_SLOT1_N----------- : $(((DATA & 0x04) >> 2))
echo RST_PE_SLOT2_N----------- : $(((DATA & 0x02) >> 1))
echo RST_PE_SLOT3_N----------- : $((DATA & 0x01))

FPGA_REG=23
DATA=$(i2cget -y $I2CBUS $FPGAADDR "$(printf '0x%x' $FPGA_REG)")
echo ----------FPGAreg$FPGA_REG-------------------------
echo RST_PE_SLOT4_N----------- : $(((DATA & 0x80) >> 7))
echo RST_PE_SLOT5_N----------- : $(((DATA & 0x40) >> 6))
echo RST_RSMRST_P0_N---------- : $(((DATA & 0x20) >> 5))
echo RST_RSMRST_P1_N---------- : $(((DATA & 0x10) >> 4))
echo RST_SYSTEM_BTN_CPLD_N---- : $(((DATA & 0x08) >> 3))
echo RST_VSBPWR_BMC_BUF_N----- : $(((DATA & 0x04) >> 2))
