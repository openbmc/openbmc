/*
 * pxaregs - tool to display and modify PXA250's registers at runtime
 *
 * (c) Copyright 2002 by M&N Logistik-Lösungen Online GmbH
 * set under the GPLv2
 *
 * $Id: pxaregs.c,v 1.14 2003/11/12 13:14:43 schurig Exp $
 *
 * Please send patches to h.schurig, working at mn-logistik.de
 * - added fix from Bernhard Nemec
 * - i2c registers from Stefan Eletzhofer
*/

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <ctype.h>

#include <linux/i2c.h>
#include <linux/i2c-dev.h>


// fd for /dev/mem
static int fd = -1;

typedef unsigned int u32;

struct reg_info {
   char *name;
   u32 addr;
   int shift;
   u32 mask;
   char type;
   char *desc;
};


static struct reg_info regs[] = {
{ "IBMR",      0x40301680,  0, 0xffffffff, 'x', "I2C Bus Monitor Register" },
{ "IBMR_SDAS", 0x40301680,  0, 0x00000001, 'x', "SDA Status" },
{ "IBMR_SCLS", 0x40301680,  1, 0x00000001, 'x', "SDA Status" },

{ "IDBR",      0x40301688,  0, 0xffffffff, 'x', "I2C Data Buffer Register" },
{ "IDBR_IDB",  0x40301688,  0, 0x000000ff, 'x', "I2C Data Buffer" },

{ "ICR",       0x40301690,  0, 0xffffffff, 'x', "I2C Control Register" },
{ "ICR_START", 0x40301690,  0,	1, 'x',  " start bit " },
{ "ICR_STOP",  0x40301690,  1,	1, 'x',  " stop bit " },
{ "ICR_ACKNAK",0x40301690,  2,	1, 'x',  " send ACK(0) or NAK(1)" },
{ "ICR_TB",    0x40301690,  3,	1, 'x',  " transfer byte bit " },
{ "ICR_MA",    0x40301690,  4,	1, 'x',  " master abort " },
{ "ICR_SCLE",  0x40301690,  5,	1, 'x',  " master clock enable " },
{ "ICR_IUE",   0x40301690,  6,	1, 'x',  " unit enable " },
{ "ICR_GCD",   0x40301690,  7,	1, 'x',  " general call disable " },
{ "ICR_ITEIE", 0x40301690,  8,	1, 'x',  " enable tx interrupts " },
{ "ICR_IRFIE", 0x40301690,  9,	1, 'x',  " enable rx interrupts " },
{ "ICR_BEIE",  0x40301690,  10,	1, 'x',  " enable bus error ints " },
{ "ICR_SSDIE", 0x40301690,  11,	1, 'x',  " slave STOP detected int enable " },
{ "ICR_ALDIE", 0x40301690,  12,	1, 'x',  " enable arbitration interrupt " },
{ "ICR_SADIE", 0x40301690,  13,	1, 'x',  " slave address detected int enable " },
{ "ICR_UR",    0x40301690,  14, 1, 'x',  " unit reset " },
{ "ICR_FM",    0x40301690,  15, 1, 'x',  " fast mode " },

{ "ISR",       0x40301698, 0, 0xffffffff, 'x', "I2C Status Register" },
{ "ISR_RWM",   0x40301698, 0, 1, 'x', " read/write mode " },
{ "ISR_ACKNAK",0x40301698, 1, 1, 'x', " ack/nak status " },
{ "ISR_UB",    0x40301698, 2, 1, 'x', " unit busy " },
{ "ISR_IBB",   0x40301698, 3, 1, 'x', " bus busy " },
{ "ISR_SSD",   0x40301698, 4, 1, 'x', " slave stop detected " },
{ "ISR_ALD",   0x40301698, 5, 1, 'x', " arbitration loss detected " },
{ "ISR_ITE",   0x40301698, 6, 1, 'x', " tx buffer empty " },
{ "ISR_IRF",   0x40301698, 7, 1, 'x', " rx buffer full " },
{ "ISR_GCAD",  0x40301698, 8, 1, 'x', " general call address detected " },
{ "ISR_SAD",   0x40301698, 9, 1, 'x', " slave address detected " },
{ "ISR_BED",   0x40301698, 10, 1, 'x', " bus error no ACK/NAK " },

{ "ISAR",      0x403016A0,  0, 0xffffffff, 'x', "I2C Slave Address Register" },
{ "ISAR_SA",   0x403016A0,  0, 0x0000007f, 'x', "I2C Slave Address" },

{ "PMCR",      0x40F00000,  0, 0xffffffff, 'x', "Power Manager Control Register (3-23)" },
{ "PMCR_IDAE", 0x40F00000,  0, 0x00000001, 'd', "PM imprecise data abort abort signal" },

{ "PSSR",      0x40F00004,  0, 0xffffffff, 'x', "Power Manager Sleep Status Register (3-29)" },
{ "PSSR_SSS",  0x40F00004,  0, 0x00000001, 'd', "PM chip was in sleep by setting sleep mode bit" },
{ "PSSR_BFS",  0x40F00004,  1, 0x00000001, 'd', "PM nBATT_FAULT has been asserted" },
{ "PSSR_VFS",  0x40F00004,  2, 0x00000001, 'd', "PM nVDD_FAULT was asserted while in Run or Idle" },
{ "PSSR_PH",   0x40F00004,  4, 0x00000001, 'd', "PM GPIO pins are held in their sleep state" },
{ "PSSR_RDH",  0x40F00004,  5, 0x00000001, 'd', "PM receivers of all input GPIO are disabled" },

{ "PSPR",      0x40F00008,  0, 0xffffffff, 'x', "Power Manager Scratch Pad Register (3-30)" },

{ "PWER",      0x40F0000C,  0, 0xffffffff, 'x', "Power Manager Wake-Up Enable Register (3-25)" },
{ "PWER_WE0",  0x40F0000C,  0, 0x00000001, 'd', "PM wake up due to GPIO 0 edge detect enabled" },
{ "PWER_WE1",  0x40F0000C,  1, 0x00000001, 'd', "PM wake up due to GPIO 1 edge detect enabled" },
{ "PWER_WE2",  0x40F0000C,  2, 0x00000001, 'd', "PM wake up due to GPIO 2 edge detect enabled" },
{ "PWER_WE3",  0x40F0000C,  3, 0x00000001, 'd', "PM wake up due to GPIO 3 edge detect enabled" },
{ "PWER_WE4",  0x40F0000C,  4, 0x00000001, 'd', "PM wake up due to GPIO 4 edge detect enabled" },
{ "PWER_WE5",  0x40F0000C,  5, 0x00000001, 'd', "PM wake up due to GPIO 5 edge detect enabled" },
{ "PWER_WE6",  0x40F0000C,  6, 0x00000001, 'd', "PM wake up due to GPIO 6 edge detect enabled" },
{ "PWER_WE7",  0x40F0000C,  7, 0x00000001, 'd', "PM wake up due to GPIO 7 edge detect enabled" },
{ "PWER_WE8",  0x40F0000C,  8, 0x00000001, 'd', "PM wake up due to GPIO 8 edge detect enabled" },
{ "PWER_WE9",  0x40F0000C,  9, 0x00000001, 'd', "PM wake up due to GPIO 9 edge detect enabled" },
{ "PWER_WE10", 0x40F0000C, 10, 0x00000001, 'd', "PM wake up due to GPIO10 edge detect enabled" },
{ "PWER_WE11", 0x40F0000C, 11, 0x00000001, 'd', "PM wake up due to GPIO11 edge detect enabled" },
{ "PWER_WE12", 0x40F0000C, 12, 0x00000001, 'd', "PM wake up due to GPIO12 edge detect enabled" },
{ "PWER_WE13", 0x40F0000C, 13, 0x00000001, 'd', "PM wake up due to GPIO13 edge detect enabled" },
{ "PWER_WE14", 0x40F0000C, 14, 0x00000001, 'd', "PM wake up due to GPIO14 edge detect enabled" },
{ "PWER_WE15", 0x40F0000C, 15, 0x00000001, 'd', "PM wake up due to GPIO15 edge detect enabled" },
{ "PWER_WERTC",0x40F0000C, 31, 0x00000001, 'd', "PM wake up due to RTC alarm enabled" },

{ "PRER",      0x40F00010,  0, 0xffffffff, 'x', "Power Manager Rising Edge Detect Enable Register (3-26)" },
{ "PRER_RE0",  0x40F00010,  0, 0x00000001, 'd', "PM wake up due to GPIO 0 rising edge detect enabled" },
{ "PRER_RE1",  0x40F00010,  1, 0x00000001, 'd', "PM wake up due to GPIO 1 rising edge detect enabled" },
{ "PRER_RE2",  0x40F00010,  2, 0x00000001, 'd', "PM wake up due to GPIO 2 rising edge detect enabled" },
{ "PRER_RE3",  0x40F00010,  3, 0x00000001, 'd', "PM wake up due to GPIO 3 rising edge detect enabled" },
{ "PRER_RE4",  0x40F00010,  4, 0x00000001, 'd', "PM wake up due to GPIO 4 rising edge detect enabled" },
{ "PRER_RE5",  0x40F00010,  5, 0x00000001, 'd', "PM wake up due to GPIO 5 rising edge detect enabled" },
{ "PRER_RE6",  0x40F00010,  6, 0x00000001, 'd', "PM wake up due to GPIO 6 rising edge detect enabled" },
{ "PRER_RE7",  0x40F00010,  7, 0x00000001, 'd', "PM wake up due to GPIO 7 rising edge detect enabled" },
{ "PRER_RE8",  0x40F00010,  8, 0x00000001, 'd', "PM wake up due to GPIO 8 rising edge detect enabled" },
{ "PRER_RE9",  0x40F00010,  9, 0x00000001, 'd', "PM wake up due to GPIO 9 rising edge detect enabled" },
{ "PRER_RE10", 0x40F00010, 10, 0x00000001, 'd', "PM wake up due to GPIO10 rising edge detect enabled" },
{ "PRER_RE11", 0x40F00010, 11, 0x00000001, 'd', "PM wake up due to GPIO11 rising edge detect enabled" },
{ "PRER_RE12", 0x40F00010, 12, 0x00000001, 'd', "PM wake up due to GPIO12 rising edge detect enabled" },
{ "PRER_RE13", 0x40F00010, 13, 0x00000001, 'd', "PM wake up due to GPIO13 rising edge detect enabled" },
{ "PRER_RE14", 0x40F00010, 14, 0x00000001, 'd', "PM wake up due to GPIO14 rising edge detect enabled" },
{ "PRER_RE15", 0x40F00010, 15, 0x00000001, 'd', "PM wake up due to GPIO15 rising edge detect enabled" },

{ "PFER",      0x40F00014,  0, 0xffffffff, 'x', "Power Manager Falling Detect Enable Register (3-27)" },
{ "PFER_FE0",  0x40F00014,  0, 0x00000001, 'd', "PM wake up due to GPIO 0 falling edge detect enabled" },
{ "PFER_FE1",  0x40F00014,  1, 0x00000001, 'd', "PM wake up due to GPIO 1 falling edge detect enabled" },
{ "PFER_FE2",  0x40F00014,  2, 0x00000001, 'd', "PM wake up due to GPIO 2 falling edge detect enabled" },
{ "PFER_FE3",  0x40F00014,  3, 0x00000001, 'd', "PM wake up due to GPIO 3 falling edge detect enabled" },
{ "PFER_FE4",  0x40F00014,  4, 0x00000001, 'd', "PM wake up due to GPIO 4 falling edge detect enabled" },
{ "PFER_FE5",  0x40F00014,  5, 0x00000001, 'd', "PM wake up due to GPIO 5 falling edge detect enabled" },
{ "PFER_FE6",  0x40F00014,  6, 0x00000001, 'd', "PM wake up due to GPIO 6 falling edge detect enabled" },
{ "PFER_FE7",  0x40F00014,  7, 0x00000001, 'd', "PM wake up due to GPIO 7 falling edge detect enabled" },
{ "PFER_FE8",  0x40F00014,  8, 0x00000001, 'd', "PM wake up due to GPIO 8 falling edge detect enabled" },
{ "PFER_FE9",  0x40F00014,  9, 0x00000001, 'd', "PM wake up due to GPIO 9 falling edge detect enabled" },
{ "PFER_FE10", 0x40F00014, 10, 0x00000001, 'd', "PM wake up due to GPIO10 falling edge detect enabled" },
{ "PFER_FE11", 0x40F00014, 11, 0x00000001, 'd', "PM wake up due to GPIO11 falling edge detect enabled" },
{ "PFER_FE12", 0x40F00014, 12, 0x00000001, 'd', "PM wake up due to GPIO12 falling edge detect enabled" },
{ "PFER_FE13", 0x40F00014, 13, 0x00000001, 'd', "PM wake up due to GPIO13 falling edge detect enabled" },
{ "PFER_FE14", 0x40F00014, 14, 0x00000001, 'd', "PM wake up due to GPIO14 falling edge detect enabled" },
{ "PFER_FE15", 0x40F00014, 15, 0x00000001, 'd', "PM wake up due to GPIO15 falling edge detect enabled" },

{ "PEDR",      0x40F00018,  0, 0xffffffff, 'x', "Power Manager Edge Detect Status Register (3-28)" },
{ "PEDR_ED0",  0x40F00018,  0, 0x00000001, 'd', "PM wake up due to edge on GPIO 0 detected" },
{ "PEDR_ED1",  0x40F00018,  1, 0x00000001, 'd', "PM wake up due to edge on GPIO 1 detected" },
{ "PEDR_ED2",  0x40F00018,  2, 0x00000001, 'd', "PM wake up due to edge on GPIO 2 detected" },
{ "PEDR_ED3",  0x40F00018,  3, 0x00000001, 'd', "PM wake up due to edge on GPIO 3 detected" },
{ "PEDR_ED4",  0x40F00018,  4, 0x00000001, 'd', "PM wake up due to edge on GPIO 4 detected" },
{ "PEDR_ED5",  0x40F00018,  5, 0x00000001, 'd', "PM wake up due to edge on GPIO 5 detected" },
{ "PEDR_ED6",  0x40F00018,  6, 0x00000001, 'd', "PM wake up due to edge on GPIO 6 detected" },
{ "PEDR_ED7",  0x40F00018,  7, 0x00000001, 'd', "PM wake up due to edge on GPIO 7 detected" },
{ "PEDR_ED8",  0x40F00018,  8, 0x00000001, 'd', "PM wake up due to edge on GPIO 8 detected" },
{ "PEDR_ED9",  0x40F00018,  9, 0x00000001, 'd', "PM wake up due to edge on GPIO 9 detected" },
{ "PEDR_ED10", 0x40F00018, 10, 0x00000001, 'd', "PM wake up due to edge on GPIO10 detected" },
{ "PEDR_ED11", 0x40F00018, 11, 0x00000001, 'd', "PM wake up due to edge on GPIO11 detected" },
{ "PEDR_ED12", 0x40F00018, 12, 0x00000001, 'd', "PM wake up due to edge on GPIO12 detected" },
{ "PEDR_ED13", 0x40F00018, 13, 0x00000001, 'd', "PM wake up due to edge on GPIO13 detected" },
{ "PEDR_ED14", 0x40F00018, 14, 0x00000001, 'd', "PM wake up due to edge on GPIO14 detected" },
{ "PEDR_ED15", 0x40F00018, 15, 0x00000001, 'd', "PM wake up due to edge on GPIO15 detected" },

{ "PCFR",      0x40F0001C,  0, 0xffffffff, 'x', "Power Manager General Configuration Register (3-24)" },
{ "PCFR_OPDE", 0x40F0001C,  0, 0x00000001, 'd', "PM stop 3.6864 MHz oscillator during sleep" },
{ "PCFR_FP",   0x40F0001C,  1, 0x00000001, 'd', "PM PCMCIA signals float during sleep" },
{ "PCFR_FS",   0x40F0001C,  2, 0x00000001, 'd', "PM static chip select signals float during sleep" },

{ "PGSR0",     0x40F00020,  0, 0xffffffff, 'x', "Power Manager GPIO Sleep State Register 0 (3-32)" },
{ "PGSR_SS0",  0x40F00020,  0, 0x00000001, 'd', "PM GPIO pin 0 is driven to 1 during sleep" },
{ "PGSR_SS1",  0x40F00020,  1, 0x00000001, 'd', "PM GPIO pin 1 is driven to 1 during sleep" },
{ "PGSR_SS2",  0x40F00020,  2, 0x00000001, 'd', "PM GPIO pin 2 is driven to 1 during sleep" },
{ "PGSR_SS3",  0x40F00020,  3, 0x00000001, 'd', "PM GPIO pin 3 is driven to 1 during sleep" },
{ "PGSR_SS4",  0x40F00020,  4, 0x00000001, 'd', "PM GPIO pin 4 is driven to 1 during sleep" },
{ "PGSR_SS5",  0x40F00020,  5, 0x00000001, 'd', "PM GPIO pin 5 is driven to 1 during sleep" },
{ "PGSR_SS6",  0x40F00020,  6, 0x00000001, 'd', "PM GPIO pin 6 is driven to 1 during sleep" },
{ "PGSR_SS7",  0x40F00020,  7, 0x00000001, 'd', "PM GPIO pin 7 is driven to 1 during sleep" },
{ "PGSR_SS8",  0x40F00020,  8, 0x00000001, 'd', "PM GPIO pin 8 is driven to 1 during sleep" },
{ "PGSR_SS9",  0x40F00020,  9, 0x00000001, 'd', "PM GPIO pin 9 is driven to 1 during sleep" },
{ "PGSR_SS10", 0x40F00020, 10, 0x00000001, 'd', "PM GPIO pin 10 is driven to 1 during sleep" },
{ "PGSR_SS11", 0x40F00020, 11, 0x00000001, 'd', "PM GPIO pin 11 is driven to 1 during sleep" },
{ "PGSR_SS12", 0x40F00020, 12, 0x00000001, 'd', "PM GPIO pin 12 is driven to 1 during sleep" },
{ "PGSR_SS13", 0x40F00020, 13, 0x00000001, 'd', "PM GPIO pin 13 is driven to 1 during sleep" },
{ "PGSR_SS14", 0x40F00020, 14, 0x00000001, 'd', "PM GPIO pin 14 is driven to 1 during sleep" },
{ "PGSR_SS15", 0x40F00020, 15, 0x00000001, 'd', "PM GPIO pin 15 is driven to 1 during sleep" },
{ "PGSR_SS16", 0x40F00020, 16, 0x00000001, 'd', "PM GPIO pin 16 is driven to 1 during sleep" },
{ "PGSR_SS17", 0x40F00020, 17, 0x00000001, 'd', "PM GPIO pin 17 is driven to 1 during sleep" },
{ "PGSR_SS18", 0x40F00020, 18, 0x00000001, 'd', "PM GPIO pin 18 is driven to 1 during sleep" },
{ "PGSR_SS19", 0x40F00020, 19, 0x00000001, 'd', "PM GPIO pin 19 is driven to 1 during sleep" },
{ "PGSR_SS20", 0x40F00020, 20, 0x00000001, 'd', "PM GPIO pin 20 is driven to 1 during sleep" },
{ "PGSR_SS21", 0x40F00020, 21, 0x00000001, 'd', "PM GPIO pin 21 is driven to 1 during sleep" },
{ "PGSR_SS22", 0x40F00020, 22, 0x00000001, 'd', "PM GPIO pin 22 is driven to 1 during sleep" },
{ "PGSR_SS23", 0x40F00020, 23, 0x00000001, 'd', "PM GPIO pin 23 is driven to 1 during sleep" },
{ "PGSR_SS24", 0x40F00020, 24, 0x00000001, 'd', "PM GPIO pin 24 is driven to 1 during sleep" },
{ "PGSR_SS25", 0x40F00020, 25, 0x00000001, 'd', "PM GPIO pin 25 is driven to 1 during sleep" },
{ "PGSR_SS26", 0x40F00020, 26, 0x00000001, 'd', "PM GPIO pin 26 is driven to 1 during sleep" },
{ "PGSR_SS27", 0x40F00020, 27, 0x00000001, 'd', "PM GPIO pin 27 is driven to 1 during sleep" },
{ "PGSR_SS28", 0x40F00020, 28, 0x00000001, 'd', "PM GPIO pin 28 is driven to 1 during sleep" },
{ "PGSR_SS29", 0x40F00020, 29, 0x00000001, 'd', "PM GPIO pin 29 is driven to 1 during sleep" },
{ "PGSR_SS30", 0x40F00020, 30, 0x00000001, 'd', "PM GPIO pin 30 is driven to 1 during sleep" },
{ "PGSR_SS31", 0x40F00020, 31, 0x00000001, 'd', "PM GPIO pin 31 is driven to 1 during sleep" },

{ "PGSR1",     0x40F00024,  0, 0xffffffff, 'x', "Power Manager GPIO Sleep State Register 1 (3-32)" },
{ "PGSR_SS32", 0x40F00024,  0, 0x00000001, 'd', "PM GPIO pin 32 is driven to 1 during sleep" },
{ "PGSR_SS33", 0x40F00024,  1, 0x00000001, 'd', "PM GPIO pin 33 is driven to 1 during sleep" },
{ "PGSR_SS34", 0x40F00024,  2, 0x00000001, 'd', "PM GPIO pin 34 is driven to 1 during sleep" },
{ "PGSR_SS35", 0x40F00024,  3, 0x00000001, 'd', "PM GPIO pin 35 is driven to 1 during sleep" },
{ "PGSR_SS36", 0x40F00024,  4, 0x00000001, 'd', "PM GPIO pin 36 is driven to 1 during sleep" },
{ "PGSR_SS37", 0x40F00024,  5, 0x00000001, 'd', "PM GPIO pin 37 is driven to 1 during sleep" },
{ "PGSR_SS38", 0x40F00024,  6, 0x00000001, 'd', "PM GPIO pin 38 is driven to 1 during sleep" },
{ "PGSR_SS39", 0x40F00024,  7, 0x00000001, 'd', "PM GPIO pin 39 is driven to 1 during sleep" },
{ "PGSR_SS40", 0x40F00024,  8, 0x00000001, 'd', "PM GPIO pin 40 is driven to 1 during sleep" },
{ "PGSR_SS41", 0x40F00024,  9, 0x00000001, 'd', "PM GPIO pin 41 is driven to 1 during sleep" },
{ "PGSR_SS42", 0x40F00024, 10, 0x00000001, 'd', "PM GPIO pin 42 is driven to 1 during sleep" },
{ "PGSR_SS43", 0x40F00024, 11, 0x00000001, 'd', "PM GPIO pin 43 is driven to 1 during sleep" },
{ "PGSR_SS44", 0x40F00024, 12, 0x00000001, 'd', "PM GPIO pin 44 is driven to 1 during sleep" },
{ "PGSR_SS45", 0x40F00024, 13, 0x00000001, 'd', "PM GPIO pin 45 is driven to 1 during sleep" },
{ "PGSR_SS46", 0x40F00024, 14, 0x00000001, 'd', "PM GPIO pin 46 is driven to 1 during sleep" },
{ "PGSR_SS47", 0x40F00024, 15, 0x00000001, 'd', "PM GPIO pin 47 is driven to 1 during sleep" },
{ "PGSR_SS48", 0x40F00024, 16, 0x00000001, 'd', "PM GPIO pin 48 is driven to 1 during sleep" },
{ "PGSR_SS49", 0x40F00024, 17, 0x00000001, 'd', "PM GPIO pin 49 is driven to 1 during sleep" },
{ "PGSR_SS50", 0x40F00024, 18, 0x00000001, 'd', "PM GPIO pin 50 is driven to 1 during sleep" },
{ "PGSR_SS51", 0x40F00024, 19, 0x00000001, 'd', "PM GPIO pin 51 is driven to 1 during sleep" },
{ "PGSR_SS52", 0x40F00024, 20, 0x00000001, 'd', "PM GPIO pin 52 is driven to 1 during sleep" },
{ "PGSR_SS53", 0x40F00024, 21, 0x00000001, 'd', "PM GPIO pin 53 is driven to 1 during sleep" },
{ "PGSR_SS54", 0x40F00024, 22, 0x00000001, 'd', "PM GPIO pin 54 is driven to 1 during sleep" },
{ "PGSR_SS55", 0x40F00024, 23, 0x00000001, 'd', "PM GPIO pin 55 is driven to 1 during sleep" },
{ "PGSR_SS56", 0x40F00024, 24, 0x00000001, 'd', "PM GPIO pin 56 is driven to 1 during sleep" },
{ "PGSR_SS57", 0x40F00024, 25, 0x00000001, 'd', "PM GPIO pin 57 is driven to 1 during sleep" },
{ "PGSR_SS58", 0x40F00024, 26, 0x00000001, 'd', "PM GPIO pin 58 is driven to 1 during sleep" },
{ "PGSR_SS59", 0x40F00024, 27, 0x00000001, 'd', "PM GPIO pin 59 is driven to 1 during sleep" },
{ "PGSR_SS60", 0x40F00024, 28, 0x00000001, 'd', "PM GPIO pin 60 is driven to 1 during sleep" },
{ "PGSR_SS61", 0x40F00024, 29, 0x00000001, 'd', "PM GPIO pin 61 is driven to 1 during sleep" },
{ "PGSR_SS62", 0x40F00024, 30, 0x00000001, 'd', "PM GPIO pin 62 is driven to 1 during sleep" },
{ "PGSR_SS63", 0x40F00024, 31, 0x00000001, 'd', "PM GPIO pin 63 is driven to 1 during sleep" },

{ "PGSR2",     0x40F00028,  0, 0xffffffff, 'x', "Power Manager GPIO Sleep State Register 2 (3-33)" },
{ "PGSR_SS64", 0x40F00028,  0, 0x00000001, 'd', "PM GPIO pin 64 is driven to 1 during sleep" },
{ "PGSR_SS65", 0x40F00028,  1, 0x00000001, 'd', "PM GPIO pin 65 is driven to 1 during sleep" },
{ "PGSR_SS66", 0x40F00028,  2, 0x00000001, 'd', "PM GPIO pin 66 is driven to 1 during sleep" },
{ "PGSR_SS67", 0x40F00028,  3, 0x00000001, 'd', "PM GPIO pin 67 is driven to 1 during sleep" },
{ "PGSR_SS68", 0x40F00028,  4, 0x00000001, 'd', "PM GPIO pin 68 is driven to 1 during sleep" },
{ "PGSR_SS69", 0x40F00028,  5, 0x00000001, 'd', "PM GPIO pin 69 is driven to 1 during sleep" },
{ "PGSR_SS70", 0x40F00028,  6, 0x00000001, 'd', "PM GPIO pin 70 is driven to 1 during sleep" },
{ "PGSR_SS71", 0x40F00028,  7, 0x00000001, 'd', "PM GPIO pin 71 is driven to 1 during sleep" },
{ "PGSR_SS72", 0x40F00028,  8, 0x00000001, 'd', "PM GPIO pin 72 is driven to 1 during sleep" },
{ "PGSR_SS73", 0x40F00028,  9, 0x00000001, 'd', "PM GPIO pin 73 is driven to 1 during sleep" },
{ "PGSR_SS74", 0x40F00028, 10, 0x00000001, 'd', "PM GPIO pin 74 is driven to 1 during sleep" },
{ "PGSR_SS75", 0x40F00028, 11, 0x00000001, 'd', "PM GPIO pin 75 is driven to 1 during sleep" },
{ "PGSR_SS76", 0x40F00028, 12, 0x00000001, 'd', "PM GPIO pin 76 is driven to 1 during sleep" },
{ "PGSR_SS77", 0x40F00028, 13, 0x00000001, 'd', "PM GPIO pin 77 is driven to 1 during sleep" },
{ "PGSR_SS78", 0x40F00028, 14, 0x00000001, 'd', "PM GPIO pin 78 is driven to 1 during sleep" },
{ "PGSR_SS79", 0x40F00028, 15, 0x00000001, 'd', "PM GPIO pin 79 is driven to 1 during sleep" },
{ "PGSR_SS80", 0x40F00028, 16, 0x00000001, 'd', "PM GPIO pin 80 is driven to 1 during sleep" },

{ "RCSR",      0x40F00030,  0, 0xffffffff, 'x', "Power Manager Reset Controller Status Register (3-34)" },
{ "RCSR_HWR",  0x40F00030,  0, 0x00000001, 'd', "PM hardware reset occurred" },
{ "RCSR_WDR",  0x40F00030,  1, 0x00000001, 'd', "PM watchdog reset occurred" },
{ "RCSR_SMR",  0x40F00030,  2, 0x00000001, 'd', "PM sleep mode occurred" },
{ "RCSR_GFR",  0x40F00030,  3, 0x00000001, 'd', "PM GPIO reset  occurred" },

// PXA255
{ "PMFW",      0x40F00034,  0, 0xffffffff, 'x', "Power Manager Fast Sleep Wakeup Register (3-31)" },
{ "PMFW_FWAKE",0x40F00034,  1, 0x00000001, 'x', "Fast Wakeup Enable" },

{ "CCCR",      0x41300000,  0, 0xffffffff, 'x', "Core Clock Configuration Register (3-35)" },
{ "CCCR_L",    0x41300000,  0, 0x0000001f, 'x', "CM crystal freq to memory freq multiplier" },
{ "CCCR_M",    0x41300000,  5, 0x00000003, 'x', "CM memory freq to run mode freq multiplier" },
{ "CCCR_N",    0x41300000,  7, 0x00000007, 'x', "CM run mode freq to turbo freq multiplier" },

{ "CKEN",      0x41300004,  0, 0xffffffff, 'x', "Clock Enable Register (3-36)" },
{ "CKEN_0",    0x41300004,  0, 0x00000001, 'd', "CM PWM0 clock enabled" },
{ "CKEN_1",    0x41300004,  1, 0x00000001, 'd', "CM PWM1 clock enabled" },
{ "CKEN_2",    0x41300004,  2, 0x00000001, 'd', "CM AC97 clock enabled" },
{ "CKEN_3",    0x41300004,  3, 0x00000001, 'd', "CM SSP clock enabled" },
{ "CKEN_5",    0x41300004,  5, 0x00000001, 'd', "CM STUART clock enabled" },
{ "CKEN_6",    0x41300004,  6, 0x00000001, 'd', "CM FFUART clock enabled" },
{ "CKEN_7",    0x41300004,  7, 0x00000001, 'd', "CM BTUART clock enabled" },
{ "CKEN_8",    0x41300004,  8, 0x00000001, 'd', "CM I2S clock enabled" },
{ "CKEN_11",   0x41300004, 11, 0x00000001, 'd', "CM USB clock enabled" },
{ "CKEN_12",   0x41300004, 12, 0x00000001, 'd', "CM MMC clock enabled" },
{ "CKEN_13",   0x41300004, 13, 0x00000001, 'd', "CM FIPC clock enabled" },
{ "CKEN_14",   0x41300004, 14, 0x00000001, 'd', "CM I2C clock enabled" },
{ "CKEN_16",   0x41300004, 16, 0x00000001, 'd', "CM LCD clock enabled" },

{ "OSCC",      0x41300008,  0, 0xffffffff, 'x', "Oscillator Configuration Register (3-38)" },
{ "OSCC_OOK",  0x41300008,  0, 0x00000001, 'd', "CM 32.768 kHz oscillator enabled and stabilized" },
{ "OSCC_OON",  0x41300008,  1, 0x00000001, 'd', "CM 32.768 kHz oscillator enabled" },

// TODO: CP14-Registers (3-37)

{ "GPLR0",    0x40E00000,  0, 0xffffffff, 'x', "GPIO Pin Level Register 0 (4-7)" },
#if defined(CONFIG_ARCH_RAMSES)
{ "GPLR0_0",  0x40E00000,  0, 0x00000001, 'd', "GPIO 0 (nc) level" },
{ "GPLR0_1",  0x40E00000,  1, 0x00000001, 'd', "GPIO 1 (nPFI) level" },
{ "GPLR0_2",  0x40E00000,  2, 0x00000001, 'd', "GPIO 2 (BAT_DATA) level" },
{ "GPLR0_3",  0x40E00000,  3, 0x00000001, 'd', "GPIO 3 (IRQ_KEY) level" },
{ "GPLR0_4",  0x40E00000,  4, 0x00000001, 'd', "GPIO 4 (IRQ_ETH) level" },
{ "GPLR0_5",  0x40E00000,  5, 0x00000001, 'd', "GPIO 5 (nc) level" },
{ "GPLR0_6",  0x40E00000,  6, 0x00000001, 'd', "GPIO 6 (MMC_CLK) level" },
{ "GPLR0_7",  0x40E00000,  7, 0x00000001, 'd', "GPIO 7 (IRQ_GSM) level" },
{ "GPLR0_8",  0x40E00000,  8, 0x00000001, 'd', "GPIO 8 (nPCC_S1_CD) level" },
{ "GPLR0_9",  0x40E00000,  9, 0x00000001, 'd', "GPIO 9 (MMC_CD) level" },
{ "GPLR0_10", 0x40E00000, 10, 0x00000001, 'd', "GPIO 10 (IRQ_RTC) level" },
{ "GPLR0_11", 0x40E00000, 11, 0x00000001, 'd', "GPIO 11 (nc 3M6) level" },
{ "GPLR0_12", 0x40E00000, 12, 0x00000001, 'd', "GPIO 12 (nc) level" },
{ "GPLR0_13", 0x40E00000, 13, 0x00000001, 'd', "GPIO 13 (IRQ_DOCK) level" },
{ "GPLR0_14", 0x40E00000, 14, 0x00000001, 'd', "GPIO 14 (nc) level" },
{ "GPLR0_15", 0x40E00000, 15, 0x00000001, 'd', "GPIO 15 (nCS1) level" },
{ "GPLR0_16", 0x40E00000, 16, 0x00000001, 'd', "GPIO 16 (PWM0) level" },
{ "GPLR0_17", 0x40E00000, 17, 0x00000001, 'd', "GPIO 17 (PWM1) level" },
{ "GPLR0_18", 0x40E00000, 18, 0x00000001, 'd', "GPIO 18 (RDY) level" },
{ "GPLR0_19", 0x40E00000, 19, 0x00000001, 'd', "GPIO 19 (nc nPCC_S0_IRQ) level" },
{ "GPLR0_20", 0x40E00000, 20, 0x00000001, 'd', "GPIO 20 (nc) level" },
{ "GPLR0_21", 0x40E00000, 21, 0x00000001, 'd', "GPIO 21 (AC97_IRQ) level" },
{ "GPLR0_22", 0x40E00000, 22, 0x00000001, 'd', "GPIO 22 (nPCC_S1_IRQ) level" },
{ "GPLR0_23", 0x40E00000, 23, 0x00000001, 'd', "GPIO 23 (UART_INTA) level" },
{ "GPLR0_24", 0x40E00000, 24, 0x00000001, 'd', "GPIO 24 (UART_INTB) level" },
{ "GPLR0_25", 0x40E00000, 25, 0x00000001, 'd', "GPIO 25 (UART_INTC) level" },
{ "GPLR0_26", 0x40E00000, 26, 0x00000001, 'd', "GPIO 26 (UART_INTD) level" },
{ "GPLR0_27", 0x40E00000, 27, 0x00000001, 'd', "GPIO 27 (nc CPLD_FREE) level" },
{ "GPLR0_28", 0x40E00000, 28, 0x00000001, 'd', "GPIO 28 (AUD_BITCLK) level" },
{ "GPLR0_29", 0x40E00000, 29, 0x00000001, 'd', "GPIO 29 (AUD_SDIN0) level" },
{ "GPLR0_30", 0x40E00000, 30, 0x00000001, 'd', "GPIO 30 (AUD_SDOUT) level" },
{ "GPLR0_31", 0x40E00000, 31, 0x00000001, 'd', "GPIO 31 (AUD_SYNC) level" },
#else
{ "GPLR0_0",  0x40E00000,  0, 0x00000001, 'd', "GPIO 0 level" },
{ "GPLR0_1",  0x40E00000,  1, 0x00000001, 'd', "GPIO 1 level" },
{ "GPLR0_2",  0x40E00000,  2, 0x00000001, 'd', "GPIO 2 level" },
{ "GPLR0_3",  0x40E00000,  3, 0x00000001, 'd', "GPIO 3 level" },
{ "GPLR0_4",  0x40E00000,  4, 0x00000001, 'd', "GPIO 4 level" },
{ "GPLR0_5",  0x40E00000,  5, 0x00000001, 'd', "GPIO 5 level" },
{ "GPLR0_6",  0x40E00000,  6, 0x00000001, 'd', "GPIO 6 level" },
{ "GPLR0_7",  0x40E00000,  7, 0x00000001, 'd', "GPIO 7 level" },
{ "GPLR0_8",  0x40E00000,  8, 0x00000001, 'd', "GPIO 8 level" },
{ "GPLR0_9",  0x40E00000,  9, 0x00000001, 'd', "GPIO 9 level" },
{ "GPLR0_10", 0x40E00000, 10, 0x00000001, 'd', "GPIO 10 level" },
{ "GPLR0_11", 0x40E00000, 11, 0x00000001, 'd', "GPIO 11 level" },
{ "GPLR0_12", 0x40E00000, 12, 0x00000001, 'd', "GPIO 12 level" },
{ "GPLR0_13", 0x40E00000, 13, 0x00000001, 'd', "GPIO 13 level" },
{ "GPLR0_14", 0x40E00000, 14, 0x00000001, 'd', "GPIO 14 level" },
{ "GPLR0_15", 0x40E00000, 15, 0x00000001, 'd', "GPIO 15 level" },
{ "GPLR0_16", 0x40E00000, 16, 0x00000001, 'd', "GPIO 16 level" },
{ "GPLR0_17", 0x40E00000, 17, 0x00000001, 'd', "GPIO 17 level" },
{ "GPLR0_18", 0x40E00000, 18, 0x00000001, 'd', "GPIO 18 level" },
{ "GPLR0_19", 0x40E00000, 19, 0x00000001, 'd', "GPIO 19 level" },
{ "GPLR0_20", 0x40E00000, 20, 0x00000001, 'd', "GPIO 20 level" },
{ "GPLR0_21", 0x40E00000, 21, 0x00000001, 'd', "GPIO 21 level" },
{ "GPLR0_22", 0x40E00000, 22, 0x00000001, 'd', "GPIO 22 level" },
{ "GPLR0_23", 0x40E00000, 23, 0x00000001, 'd', "GPIO 23 level" },
{ "GPLR0_24", 0x40E00000, 24, 0x00000001, 'd', "GPIO 24 level" },
{ "GPLR0_25", 0x40E00000, 25, 0x00000001, 'd', "GPIO 25 level" },
{ "GPLR0_26", 0x40E00000, 26, 0x00000001, 'd', "GPIO 26 level" },
{ "GPLR0_27", 0x40E00000, 27, 0x00000001, 'd', "GPIO 27 level" },
{ "GPLR0_28", 0x40E00000, 28, 0x00000001, 'd', "GPIO 28 level" },
{ "GPLR0_29", 0x40E00000, 29, 0x00000001, 'd', "GPIO 29 level" },
{ "GPLR0_30", 0x40E00000, 30, 0x00000001, 'd', "GPIO 30 level" },
{ "GPLR0_31", 0x40E00000, 31, 0x00000001, 'd', "GPIO 31 level" },
#endif

{ "GPLR1",    0x40E00004,  0, 0xffffffff, 'x', "GPIO Level Register 1 (4-8)" },
#if defined(CONFIG_ARCH_RAMSES)
{ "GPLR1_32", 0x40E00004,  0, 0x00000001, 'd', "GPIO 32 (AUD_SDIN1) level" },
{ "GPLR1_33", 0x40E00004,  1, 0x00000001, 'd', "GPIO 33 (nCS5) level" },
{ "GPLR1_34", 0x40E00004,  2, 0x00000001, 'd', "GPIO 34 (FF_RXD) level" },
{ "GPLR1_35", 0x40E00004,  3, 0x00000001, 'd', "GPIO 35 (FF_CTS) level" },
{ "GPLR1_36", 0x40E00004,  4, 0x00000001, 'd', "GPIO 36 (FF_DCD) level" },
{ "GPLR1_37", 0x40E00004,  5, 0x00000001, 'd', "GPIO 37 (FF_DSR) level" },
{ "GPLR1_38", 0x40E00004,  6, 0x00000001, 'd', "GPIO 38 (FF_RI) level" },
{ "GPLR1_39", 0x40E00004,  7, 0x00000001, 'd', "GPIO 39 (FF_TXD) level" },
{ "GPLR1_40", 0x40E00004,  8, 0x00000001, 'd', "GPIO 40 (FF_DTR) level" },
{ "GPLR1_41", 0x40E00004,  9, 0x00000001, 'd', "GPIO 41 (FF_RTS) level" },
{ "GPLR1_42", 0x40E00004, 10, 0x00000001, 'd', "GPIO 42 (BT_RXD) level" },
{ "GPLR1_43", 0x40E00004, 11, 0x00000001, 'd', "GPIO 43 (BT_TXD) level" },
{ "GPLR1_44", 0x40E00004, 12, 0x00000001, 'd', "GPIO 44 (BT_CTS) level" },
{ "GPLR1_45", 0x40E00004, 13, 0x00000001, 'd', "GPIO 45 (BT_RTS) level" },
{ "GPLR1_46", 0x40E00004, 14, 0x00000001, 'd', "GPIO 46 (IR_RXD) level" },
{ "GPLR1_47", 0x40E00004, 15, 0x00000001, 'd', "GPIO 47 (IR_TXD) level" },
{ "GPLR1_48", 0x40E00004, 16, 0x00000001, 'd', "GPIO 48 (nPOE) level" },
{ "GPLR1_49", 0x40E00004, 17, 0x00000001, 'd', "GPIO 49 (nPWE) level" },
{ "GPLR1_50", 0x40E00004, 18, 0x00000001, 'd', "GPIO 50 (nPIOR) level" },
{ "GPLR1_51", 0x40E00004, 19, 0x00000001, 'd', "GPIO 51 (nPIOW) level" },
{ "GPLR1_52", 0x40E00004, 20, 0x00000001, 'd', "GPIO 52 (nPCE1) level" },
{ "GPLR1_53", 0x40E00004, 21, 0x00000001, 'd', "GPIO 53 (nPCE2) level" },
{ "GPLR1_54", 0x40E00004, 22, 0x00000001, 'd', "GPIO 54 (nPKTSEL) level" },
{ "GPLR1_55", 0x40E00004, 23, 0x00000001, 'd', "GPIO 55 (nPREG) level" },
{ "GPLR1_56", 0x40E00004, 24, 0x00000001, 'd', "GPIO 56 (nPWAIT) level" },
{ "GPLR1_57", 0x40E00004, 25, 0x00000001, 'd', "GPIO 57 (nIOIS16) level" },
{ "GPLR1_58", 0x40E00004, 26, 0x00000001, 'd', "GPIO 58 (LDD0) level" },
{ "GPLR1_59", 0x40E00004, 27, 0x00000001, 'd', "GPIO 59 (LDD1) level" },
{ "GPLR1_60", 0x40E00004, 28, 0x00000001, 'd', "GPIO 60 (LDD2) level" },
{ "GPLR1_61", 0x40E00004, 29, 0x00000001, 'd', "GPIO 61 (LDD3) level" },
{ "GPLR1_62", 0x40E00004, 30, 0x00000001, 'd', "GPIO 62 (LDD4) level" },
{ "GPLR1_63", 0x40E00004, 31, 0x00000001, 'd', "GPIO 63 (LDD5) level" },
#else
{ "GPLR1_32", 0x40E00004,  0, 0x00000001, 'd', "GPIO 32 level" },
{ "GPLR1_33", 0x40E00004,  1, 0x00000001, 'd', "GPIO 33 level" },
{ "GPLR1_34", 0x40E00004,  2, 0x00000001, 'd', "GPIO 34 level" },
{ "GPLR1_35", 0x40E00004,  3, 0x00000001, 'd', "GPIO 35 level" },
{ "GPLR1_36", 0x40E00004,  4, 0x00000001, 'd', "GPIO 36 level" },
{ "GPLR1_37", 0x40E00004,  5, 0x00000001, 'd', "GPIO 37 level" },
{ "GPLR1_38", 0x40E00004,  6, 0x00000001, 'd', "GPIO 38 level" },
{ "GPLR1_39", 0x40E00004,  7, 0x00000001, 'd', "GPIO 39 level" },
{ "GPLR1_40", 0x40E00004,  8, 0x00000001, 'd', "GPIO 40 level" },
{ "GPLR1_41", 0x40E00004,  9, 0x00000001, 'd', "GPIO 41 level" },
{ "GPLR1_42", 0x40E00004, 10, 0x00000001, 'd', "GPIO 42 level" },
{ "GPLR1_43", 0x40E00004, 11, 0x00000001, 'd', "GPIO 43 level" },
{ "GPLR1_44", 0x40E00004, 12, 0x00000001, 'd', "GPIO 44 level" },
{ "GPLR1_45", 0x40E00004, 13, 0x00000001, 'd', "GPIO 45 level" },
{ "GPLR1_46", 0x40E00004, 14, 0x00000001, 'd', "GPIO 46 level" },
{ "GPLR1_47", 0x40E00004, 15, 0x00000001, 'd', "GPIO 47 level" },
{ "GPLR1_48", 0x40E00004, 16, 0x00000001, 'd', "GPIO 48 level" },
{ "GPLR1_49", 0x40E00004, 17, 0x00000001, 'd', "GPIO 49 level" },
{ "GPLR1_50", 0x40E00004, 18, 0x00000001, 'd', "GPIO 50 level" },
{ "GPLR1_51", 0x40E00004, 19, 0x00000001, 'd', "GPIO 51 level" },
{ "GPLR1_52", 0x40E00004, 20, 0x00000001, 'd', "GPIO 52 level" },
{ "GPLR1_53", 0x40E00004, 21, 0x00000001, 'd', "GPIO 53 level" },
{ "GPLR1_54", 0x40E00004, 22, 0x00000001, 'd', "GPIO 54 level" },
{ "GPLR1_55", 0x40E00004, 23, 0x00000001, 'd', "GPIO 55 level" },
{ "GPLR1_56", 0x40E00004, 24, 0x00000001, 'd', "GPIO 56 level" },
{ "GPLR1_57", 0x40E00004, 25, 0x00000001, 'd', "GPIO 57 level" },
{ "GPLR1_58", 0x40E00004, 26, 0x00000001, 'd', "GPIO 58 level" },
{ "GPLR1_59", 0x40E00004, 27, 0x00000001, 'd', "GPIO 59 level" },
{ "GPLR1_60", 0x40E00004, 28, 0x00000001, 'd', "GPIO 60 level" },
{ "GPLR1_61", 0x40E00004, 29, 0x00000001, 'd', "GPIO 61 level" },
{ "GPLR1_62", 0x40E00004, 30, 0x00000001, 'd', "GPIO 62 level" },
{ "GPLR1_63", 0x40E00004, 31, 0x00000001, 'd', "GPIO 63 level" },
#endif

{ "GPLR2",    0x40E00008,  0, 0xffffffff, 'x', "GPIO Level Register 2 (4-8)" },
#if defined(CONFIG_ARCH_RAMSES)
{ "GPLR2_64", 0x40E00008,  0, 0x00000001, 'd', "GPIO 64 (LDD6) level" },
{ "GPLR2_65", 0x40E00008,  1, 0x00000001, 'd', "GPIO 65 (LDD7) level" },
{ "GPLR2_66", 0x40E00008,  2, 0x00000001, 'd', "GPIO 66 (nc) level" },
{ "GPLR2_67", 0x40E00008,  3, 0x00000001, 'd', "GPIO 67 (nc) level" },
{ "GPLR2_68", 0x40E00008,  4, 0x00000001, 'd', "GPIO 68 (nc) level" },
{ "GPLR2_69", 0x40E00008,  5, 0x00000001, 'd', "GPIO 69 (nc) level" },
{ "GPLR2_70", 0x40E00008,  6, 0x00000001, 'd', "GPIO 70 (nc) level" },
{ "GPLR2_71", 0x40E00008,  7, 0x00000001, 'd', "GPIO 71 (nc) level" },
{ "GPLR2_72", 0x40E00008,  8, 0x00000001, 'd', "GPIO 72 (nc) level" },
{ "GPLR2_73", 0x40E00008,  9, 0x00000001, 'd', "GPIO 73 (nc) level" },
{ "GPLR2_74", 0x40E00008, 10, 0x00000001, 'd', "GPIO 74 (FCLK) level" },
{ "GPLR2_75", 0x40E00008, 11, 0x00000001, 'd', "GPIO 75 (LCLK) level" },
{ "GPLR2_76", 0x40E00008, 12, 0x00000001, 'd', "GPIO 76 (PCLK) level" },
{ "GPLR2_77", 0x40E00008, 13, 0x00000001, 'd', "GPIO 77 (BIAS) level" },
{ "GPLR2_78", 0x40E00008, 14, 0x00000001, 'd', "GPIO 78 (nCS2) level" },
{ "GPLR2_79", 0x40E00008, 15, 0x00000001, 'd', "GPIO 79 (nCS3) level" },
{ "GPLR2_80", 0x40E00008, 16, 0x00000001, 'd', "GPIO 80 (nCS4) level" },
{ "GPLR2_81", 0x40E00008, 17, 0x00000001, 'd', "GPIO 81 (nc) level" },
{ "GPLR2_82", 0x40E00008, 18, 0x00000001, 'd', "GPIO 82 (nc) level" },
{ "GPLR2_83", 0x40E00008, 19, 0x00000001, 'd', "GPIO 83 (nc) level" },
{ "GPLR2_84", 0x40E00008, 20, 0x00000001, 'd', "GPIO 84 (nc) level" },
#else
{ "GPLR2_64", 0x40E00008,  0, 0x00000001, 'd', "GPIO 64 level" },
{ "GPLR2_65", 0x40E00008,  1, 0x00000001, 'd', "GPIO 65 level" },
{ "GPLR2_66", 0x40E00008,  2, 0x00000001, 'd', "GPIO 66 level" },
{ "GPLR2_67", 0x40E00008,  3, 0x00000001, 'd', "GPIO 67 level" },
{ "GPLR2_68", 0x40E00008,  4, 0x00000001, 'd', "GPIO 68 level" },
{ "GPLR2_69", 0x40E00008,  5, 0x00000001, 'd', "GPIO 69 level" },
{ "GPLR2_70", 0x40E00008,  6, 0x00000001, 'd', "GPIO 70 level" },
{ "GPLR2_71", 0x40E00008,  7, 0x00000001, 'd', "GPIO 71 level" },
{ "GPLR2_72", 0x40E00008,  8, 0x00000001, 'd', "GPIO 72 level" },
{ "GPLR2_73", 0x40E00008,  9, 0x00000001, 'd', "GPIO 73 level" },
{ "GPLR2_74", 0x40E00008, 10, 0x00000001, 'd', "GPIO 74 level" },
{ "GPLR2_75", 0x40E00008, 11, 0x00000001, 'd', "GPIO 75 level" },
{ "GPLR2_76", 0x40E00008, 12, 0x00000001, 'd', "GPIO 76 level" },
{ "GPLR2_77", 0x40E00008, 13, 0x00000001, 'd', "GPIO 77 level" },
{ "GPLR2_78", 0x40E00008, 14, 0x00000001, 'd', "GPIO 78 level" },
{ "GPLR2_79", 0x40E00008, 15, 0x00000001, 'd', "GPIO 79 level" },
{ "GPLR2_80", 0x40E00008, 16, 0x00000001, 'd', "GPIO 80 level" },
{ "GPLR2_81", 0x40E00008, 17, 0x00000001, 'd', "GPIO 81 level" },
{ "GPLR2_82", 0x40E00008, 18, 0x00000001, 'd', "GPIO 82 level" },
{ "GPLR2_83", 0x40E00008, 19, 0x00000001, 'd', "GPIO 83 level" },
{ "GPLR2_84", 0x40E00008, 20, 0x00000001, 'd', "GPIO 84 level" },
#endif

{ "GPDR0",    0x40E0000C,  0, 0xffffffff, 'x', "GPIO Direction Register 0 (4-9)" },
{ "GPDR0_0",  0x40E0000C,  0, 0x00000001, 'd', "GPIO 0 i/o direction (1=output)" },
{ "GPDR0_1",  0x40E0000C,  1, 0x00000001, 'd', "GPIO 1 i/o direction (1=output)" },
{ "GPDR0_2",  0x40E0000C,  2, 0x00000001, 'd', "GPIO 2 i/o direction (1=output)" },
{ "GPDR0_3",  0x40E0000C,  3, 0x00000001, 'd', "GPIO 3 i/o direction (1=output)" },
{ "GPDR0_4",  0x40E0000C,  4, 0x00000001, 'd', "GPIO 4 i/o direction (1=output)" },
{ "GPDR0_5",  0x40E0000C,  5, 0x00000001, 'd', "GPIO 5 i/o direction (1=output)" },
{ "GPDR0_6",  0x40E0000C,  6, 0x00000001, 'd', "GPIO 6 i/o direction (1=output)" },
{ "GPDR0_7",  0x40E0000C,  7, 0x00000001, 'd', "GPIO 7 i/o direction (1=output)" },
{ "GPDR0_8",  0x40E0000C,  8, 0x00000001, 'd', "GPIO 8 i/o direction (1=output)" },
{ "GPDR0_9",  0x40E0000C,  9, 0x00000001, 'd', "GPIO 9 i/o direction (1=output)" },
{ "GPDR0_10", 0x40E0000C, 10, 0x00000001, 'd', "GPIO 10 i/o direction (1=output)" },
{ "GPDR0_11", 0x40E0000C, 11, 0x00000001, 'd', "GPIO 11 i/o direction (1=output)" },
{ "GPDR0_12", 0x40E0000C, 12, 0x00000001, 'd', "GPIO 12 i/o direction (1=output)" },
{ "GPDR0_13", 0x40E0000C, 13, 0x00000001, 'd', "GPIO 13 i/o direction (1=output)" },
{ "GPDR0_14", 0x40E0000C, 14, 0x00000001, 'd', "GPIO 14 i/o direction (1=output)" },
{ "GPDR0_15", 0x40E0000C, 15, 0x00000001, 'd', "GPIO 15 i/o direction (1=output)" },
{ "GPDR0_16", 0x40E0000C, 16, 0x00000001, 'd', "GPIO 16 i/o direction (1=output)" },
{ "GPDR0_17", 0x40E0000C, 17, 0x00000001, 'd', "GPIO 17 i/o direction (1=output)" },
{ "GPDR0_18", 0x40E0000C, 18, 0x00000001, 'd', "GPIO 18 i/o direction (1=output)" },
{ "GPDR0_19", 0x40E0000C, 19, 0x00000001, 'd', "GPIO 19 i/o direction (1=output)" },
{ "GPDR0_20", 0x40E0000C, 20, 0x00000001, 'd', "GPIO 20 i/o direction (1=output)" },
{ "GPDR0_21", 0x40E0000C, 21, 0x00000001, 'd', "GPIO 21 i/o direction (1=output)" },
{ "GPDR0_22", 0x40E0000C, 22, 0x00000001, 'd', "GPIO 22 i/o direction (1=output)" },
{ "GPDR0_23", 0x40E0000C, 23, 0x00000001, 'd', "GPIO 23 i/o direction (1=output)" },
{ "GPDR0_24", 0x40E0000C, 24, 0x00000001, 'd', "GPIO 24 i/o direction (1=output)" },
{ "GPDR0_25", 0x40E0000C, 25, 0x00000001, 'd', "GPIO 25 i/o direction (1=output)" },
{ "GPDR0_26", 0x40E0000C, 26, 0x00000001, 'd', "GPIO 26 i/o direction (1=output)" },
{ "GPDR0_27", 0x40E0000C, 27, 0x00000001, 'd', "GPIO 27 i/o direction (1=output)" },
{ "GPDR0_28", 0x40E0000C, 28, 0x00000001, 'd', "GPIO 28 i/o direction (1=output)" },
{ "GPDR0_29", 0x40E0000C, 29, 0x00000001, 'd', "GPIO 29 i/o direction (1=output)" },
{ "GPDR0_30", 0x40E0000C, 30, 0x00000001, 'd', "GPIO 30 i/o direction (1=output)" },
{ "GPDR0_31", 0x40E0000C, 31, 0x00000001, 'd', "GPIO 31 i/o direction (1=output)" },

{ "GPDR1",    0x40E00010,  0, 0xffffffff, 'x', "GPIO Direction Register 1 (4-9)" },
{ "GPDR1_32", 0x40E00010,  0, 0x00000001, 'd', "GPIO 32 i/o direction (1=output)" },
{ "GPDR1_33", 0x40E00010,  1, 0x00000001, 'd', "GPIO 33 i/o direction (1=output)" },
{ "GPDR1_34", 0x40E00010,  2, 0x00000001, 'd', "GPIO 34 i/o direction (1=output)" },
{ "GPDR1_35", 0x40E00010,  3, 0x00000001, 'd', "GPIO 35 i/o direction (1=output)" },
{ "GPDR1_36", 0x40E00010,  4, 0x00000001, 'd', "GPIO 36 i/o direction (1=output)" },
{ "GPDR1_37", 0x40E00010,  5, 0x00000001, 'd', "GPIO 37 i/o direction (1=output)" },
{ "GPDR1_38", 0x40E00010,  6, 0x00000001, 'd', "GPIO 38 i/o direction (1=output)" },
{ "GPDR1_39", 0x40E00010,  7, 0x00000001, 'd', "GPIO 39 i/o direction (1=output)" },
{ "GPDR1_40", 0x40E00010,  8, 0x00000001, 'd', "GPIO 40 i/o direction (1=output)" },
{ "GPDR1_41", 0x40E00010,  9, 0x00000001, 'd', "GPIO 41 i/o direction (1=output)" },
{ "GPDR1_42", 0x40E00010, 10, 0x00000001, 'd', "GPIO 42 i/o direction (1=output)" },
{ "GPDR1_43", 0x40E00010, 11, 0x00000001, 'd', "GPIO 43 i/o direction (1=output)" },
{ "GPDR1_44", 0x40E00010, 12, 0x00000001, 'd', "GPIO 44 i/o direction (1=output)" },
{ "GPDR1_45", 0x40E00010, 13, 0x00000001, 'd', "GPIO 45 i/o direction (1=output)" },
{ "GPDR1_46", 0x40E00010, 14, 0x00000001, 'd', "GPIO 46 i/o direction (1=output)" },
{ "GPDR1_47", 0x40E00010, 15, 0x00000001, 'd', "GPIO 47 i/o direction (1=output)" },
{ "GPDR1_48", 0x40E00010, 16, 0x00000001, 'd', "GPIO 48 i/o direction (1=output)" },
{ "GPDR1_49", 0x40E00010, 17, 0x00000001, 'd', "GPIO 49 i/o direction (1=output)" },
{ "GPDR1_50", 0x40E00010, 18, 0x00000001, 'd', "GPIO 50 i/o direction (1=output)" },
{ "GPDR1_51", 0x40E00010, 19, 0x00000001, 'd', "GPIO 51 i/o direction (1=output)" },
{ "GPDR1_52", 0x40E00010, 20, 0x00000001, 'd', "GPIO 52 i/o direction (1=output)" },
{ "GPDR1_53", 0x40E00010, 21, 0x00000001, 'd', "GPIO 53 i/o direction (1=output)" },
{ "GPDR1_54", 0x40E00010, 22, 0x00000001, 'd', "GPIO 54 i/o direction (1=output)" },
{ "GPDR1_55", 0x40E00010, 23, 0x00000001, 'd', "GPIO 55 i/o direction (1=output)" },
{ "GPDR1_56", 0x40E00010, 24, 0x00000001, 'd', "GPIO 56 i/o direction (1=output)" },
{ "GPDR1_57", 0x40E00010, 25, 0x00000001, 'd', "GPIO 57 i/o direction (1=output)" },
{ "GPDR1_58", 0x40E00010, 26, 0x00000001, 'd', "GPIO 58 i/o direction (1=output)" },
{ "GPDR1_59", 0x40E00010, 27, 0x00000001, 'd', "GPIO 59 i/o direction (1=output)" },
{ "GPDR1_60", 0x40E00010, 28, 0x00000001, 'd', "GPIO 60 i/o direction (1=output)" },
{ "GPDR1_61", 0x40E00010, 29, 0x00000001, 'd', "GPIO 61 i/o direction (1=output)" },
{ "GPDR1_62", 0x40E00010, 30, 0x00000001, 'd', "GPIO 62 i/o direction (1=output)" },
{ "GPDR1_63", 0x40E00010, 31, 0x00000001, 'd', "GPIO 63 i/o direction (1=output)" },

{ "GPDR2",    0x40E00014,  0, 0xffffffff, 'x', "GPIO Direction Register 2 (4-9)" },
{ "GPDR2_64", 0x40E00014,  0, 0x00000001, 'd', "GPIO 64 i/o direction (1=output)" },
{ "GPDR2_65", 0x40E00014,  1, 0x00000001, 'd', "GPIO 65 i/o direction (1=output)" },
{ "GPDR2_66", 0x40E00014,  2, 0x00000001, 'd', "GPIO 66 i/o direction (1=output)" },
{ "GPDR2_67", 0x40E00014,  3, 0x00000001, 'd', "GPIO 67 i/o direction (1=output)" },
{ "GPDR2_68", 0x40E00014,  4, 0x00000001, 'd', "GPIO 68 i/o direction (1=output)" },
{ "GPDR2_69", 0x40E00014,  5, 0x00000001, 'd', "GPIO 69 i/o direction (1=output)" },
{ "GPDR2_70", 0x40E00014,  6, 0x00000001, 'd', "GPIO 70 i/o direction (1=output)" },
{ "GPDR2_71", 0x40E00014,  7, 0x00000001, 'd', "GPIO 71 i/o direction (1=output)" },
{ "GPDR2_72", 0x40E00014,  8, 0x00000001, 'd', "GPIO 72 i/o direction (1=output)" },
{ "GPDR2_73", 0x40E00014,  9, 0x00000001, 'd', "GPIO 73 i/o direction (1=output)" },
{ "GPDR2_74", 0x40E00014, 10, 0x00000001, 'd', "GPIO 74 i/o direction (1=output)" },
{ "GPDR2_75", 0x40E00014, 11, 0x00000001, 'd', "GPIO 75 i/o direction (1=output)" },
{ "GPDR2_76", 0x40E00014, 12, 0x00000001, 'd', "GPIO 76 i/o direction (1=output)" },
{ "GPDR2_77", 0x40E00014, 13, 0x00000001, 'd', "GPIO 77 i/o direction (1=output)" },
{ "GPDR2_78", 0x40E00014, 14, 0x00000001, 'd', "GPIO 78 i/o direction (1=output)" },
{ "GPDR2_79", 0x40E00014, 15, 0x00000001, 'd', "GPIO 79 i/o direction (1=output)" },
{ "GPDR2_80", 0x40E00014, 16, 0x00000001, 'd', "GPIO 80 i/o direction (1=output)" },
{ "GPDR2_81", 0x40E00014, 17, 0x00000001, 'd', "GPIO 81 i/o direction (1=output)" },
{ "GPDR2_82", 0x40E00014, 18, 0x00000001, 'd', "GPIO 82 i/o direction (1=output)" },
{ "GPDR2_83", 0x40E00014, 19, 0x00000001, 'd', "GPIO 83 i/o direction (1=output)" },
{ "GPDR2_84", 0x40E00014, 20, 0x00000001, 'd', "GPIO 84 i/o direction (1=output)" },

{ "GPSR0",    0x40E00018,  0, 0xffffffff, 'x', "GPIO Set Register 0 (4-10)" },
{ "GPSR0_0",  0x40E00018,  0, 0x00000001, 'd', "GPIO 0 set" },
{ "GPSR0_1",  0x40E00018,  1, 0x00000001, 'd', "GPIO 1 set" },
{ "GPSR0_2",  0x40E00018,  2, 0x00000001, 'd', "GPIO 2 set" },
{ "GPSR0_3",  0x40E00018,  3, 0x00000001, 'd', "GPIO 3 set" },
{ "GPSR0_4",  0x40E00018,  4, 0x00000001, 'd', "GPIO 4 set" },
{ "GPSR0_5",  0x40E00018,  5, 0x00000001, 'd', "GPIO 5 set" },
{ "GPSR0_6",  0x40E00018,  6, 0x00000001, 'd', "GPIO 6 set" },
{ "GPSR0_7",  0x40E00018,  7, 0x00000001, 'd', "GPIO 7 set" },
{ "GPSR0_8",  0x40E00018,  8, 0x00000001, 'd', "GPIO 8 set" },
{ "GPSR0_9",  0x40E00018,  9, 0x00000001, 'd', "GPIO 9 set" },
{ "GPSR0_10", 0x40E00018, 10, 0x00000001, 'd', "GPIO 10 set" },
{ "GPSR0_11", 0x40E00018, 11, 0x00000001, 'd', "GPIO 11 set" },
{ "GPSR0_12", 0x40E00018, 12, 0x00000001, 'd', "GPIO 12 set" },
{ "GPSR0_13", 0x40E00018, 13, 0x00000001, 'd', "GPIO 13 set" },
{ "GPSR0_14", 0x40E00018, 14, 0x00000001, 'd', "GPIO 14 set" },
{ "GPSR0_15", 0x40E00018, 15, 0x00000001, 'd', "GPIO 15 set" },
{ "GPSR0_16", 0x40E00018, 16, 0x00000001, 'd', "GPIO 16 set" },
{ "GPSR0_17", 0x40E00018, 17, 0x00000001, 'd', "GPIO 17 set" },
{ "GPSR0_18", 0x40E00018, 18, 0x00000001, 'd', "GPIO 18 set" },
{ "GPSR0_19", 0x40E00018, 19, 0x00000001, 'd', "GPIO 19 set" },
{ "GPSR0_20", 0x40E00018, 20, 0x00000001, 'd', "GPIO 20 set" },
{ "GPSR0_21", 0x40E00018, 21, 0x00000001, 'd', "GPIO 21 set" },
{ "GPSR0_22", 0x40E00018, 22, 0x00000001, 'd', "GPIO 22 set" },
{ "GPSR0_23", 0x40E00018, 23, 0x00000001, 'd', "GPIO 23 set" },
{ "GPSR0_24", 0x40E00018, 24, 0x00000001, 'd', "GPIO 24 set" },
{ "GPSR0_25", 0x40E00018, 25, 0x00000001, 'd', "GPIO 25 set" },
{ "GPSR0_26", 0x40E00018, 26, 0x00000001, 'd', "GPIO 26 set" },
{ "GPSR0_27", 0x40E00018, 27, 0x00000001, 'd', "GPIO 27 set" },
{ "GPSR0_28", 0x40E00018, 28, 0x00000001, 'd', "GPIO 28 set" },
{ "GPSR0_29", 0x40E00018, 29, 0x00000001, 'd', "GPIO 29 set" },
{ "GPSR0_30", 0x40E00018, 30, 0x00000001, 'd', "GPIO 30 set" },
{ "GPSR0_31", 0x40E00018, 31, 0x00000001, 'd', "GPIO 31 set" },

{ "GPSR1",    0x40E0001C,  0, 0xffffffff, 'x', "GPIO Set Register 1 (4-10)" },
{ "GPSR1_32", 0x40E0001C,  0, 0x00000001, 'd', "GPIO 32 set" },
{ "GPSR1_33", 0x40E0001C,  1, 0x00000001, 'd', "GPIO 33 set" },
{ "GPSR1_34", 0x40E0001C,  2, 0x00000001, 'd', "GPIO 34 set" },
{ "GPSR1_35", 0x40E0001C,  3, 0x00000001, 'd', "GPIO 35 set" },
{ "GPSR1_36", 0x40E0001C,  4, 0x00000001, 'd', "GPIO 36 set" },
{ "GPSR1_37", 0x40E0001C,  5, 0x00000001, 'd', "GPIO 37 set" },
{ "GPSR1_38", 0x40E0001C,  6, 0x00000001, 'd', "GPIO 38 set" },
{ "GPSR1_39", 0x40E0001C,  7, 0x00000001, 'd', "GPIO 39 set" },
{ "GPSR1_40", 0x40E0001C,  8, 0x00000001, 'd', "GPIO 40 set" },
{ "GPSR1_41", 0x40E0001C,  9, 0x00000001, 'd', "GPIO 41 set" },
{ "GPSR1_42", 0x40E0001C, 10, 0x00000001, 'd', "GPIO 42 set" },
{ "GPSR1_43", 0x40E0001C, 11, 0x00000001, 'd', "GPIO 43 set" },
{ "GPSR1_44", 0x40E0001C, 12, 0x00000001, 'd', "GPIO 44 set" },
{ "GPSR1_45", 0x40E0001C, 13, 0x00000001, 'd', "GPIO 45 set" },
{ "GPSR1_46", 0x40E0001C, 14, 0x00000001, 'd', "GPIO 46 set" },
{ "GPSR1_47", 0x40E0001C, 15, 0x00000001, 'd', "GPIO 47 set" },
{ "GPSR1_48", 0x40E0001C, 16, 0x00000001, 'd', "GPIO 48 set" },
{ "GPSR1_49", 0x40E0001C, 17, 0x00000001, 'd', "GPIO 49 set" },
{ "GPSR1_50", 0x40E0001C, 18, 0x00000001, 'd', "GPIO 50 set" },
{ "GPSR1_51", 0x40E0001C, 19, 0x00000001, 'd', "GPIO 51 set" },
{ "GPSR1_52", 0x40E0001C, 20, 0x00000001, 'd', "GPIO 52 set" },
{ "GPSR1_53", 0x40E0001C, 21, 0x00000001, 'd', "GPIO 53 set" },
{ "GPSR1_54", 0x40E0001C, 22, 0x00000001, 'd', "GPIO 54 set" },
{ "GPSR1_55", 0x40E0001C, 23, 0x00000001, 'd', "GPIO 55 set" },
{ "GPSR1_56", 0x40E0001C, 24, 0x00000001, 'd', "GPIO 56 set" },
{ "GPSR1_57", 0x40E0001C, 25, 0x00000001, 'd', "GPIO 57 set" },
{ "GPSR1_58", 0x40E0001C, 26, 0x00000001, 'd', "GPIO 58 set" },
{ "GPSR1_59", 0x40E0001C, 27, 0x00000001, 'd', "GPIO 59 set" },
{ "GPSR1_60", 0x40E0001C, 28, 0x00000001, 'd', "GPIO 60 set" },
{ "GPSR1_61", 0x40E0001C, 29, 0x00000001, 'd', "GPIO 61 set" },
{ "GPSR1_62", 0x40E0001C, 30, 0x00000001, 'd', "GPIO 62 set" },
{ "GPSR1_63", 0x40E0001C, 31, 0x00000001, 'd', "GPIO 63 set" },

{ "GPSR2",    0x40E00020,  0, 0xffffffff, 'x', "GPIO Set Register 2 (4-11)" },
{ "GPSR2_64", 0x40E00020,  0, 0x00000001, 'd', "GPIO 64 set" },
{ "GPSR2_65", 0x40E00020,  1, 0x00000001, 'd', "GPIO 65 set" },
{ "GPSR2_66", 0x40E00020,  2, 0x00000001, 'd', "GPIO 66 set" },
{ "GPSR2_67", 0x40E00020,  3, 0x00000001, 'd', "GPIO 67 set" },
{ "GPSR2_68", 0x40E00020,  4, 0x00000001, 'd', "GPIO 68 set" },
{ "GPSR2_69", 0x40E00020,  5, 0x00000001, 'd', "GPIO 69 set" },
{ "GPSR2_70", 0x40E00020,  6, 0x00000001, 'd', "GPIO 70 set" },
{ "GPSR2_71", 0x40E00020,  7, 0x00000001, 'd', "GPIO 71 set" },
{ "GPSR2_72", 0x40E00020,  8, 0x00000001, 'd', "GPIO 72 set" },
{ "GPSR2_73", 0x40E00020,  9, 0x00000001, 'd', "GPIO 73 set" },
{ "GPSR2_74", 0x40E00020, 10, 0x00000001, 'd', "GPIO 74 set" },
{ "GPSR2_75", 0x40E00020, 11, 0x00000001, 'd', "GPIO 75 set" },
{ "GPSR2_76", 0x40E00020, 12, 0x00000001, 'd', "GPIO 76 set" },
{ "GPSR2_77", 0x40E00020, 13, 0x00000001, 'd', "GPIO 77 set" },
{ "GPSR2_78", 0x40E00020, 14, 0x00000001, 'd', "GPIO 78 set" },
{ "GPSR2_79", 0x40E00020, 15, 0x00000001, 'd', "GPIO 79 set" },
{ "GPSR2_80", 0x40E00020, 16, 0x00000001, 'd', "GPIO 80 set" },
{ "GPSR2_81", 0x40E00020, 17, 0x00000001, 'd', "GPIO 81 set" },
{ "GPSR2_82", 0x40E00020, 18, 0x00000001, 'd', "GPIO 82 set" },
{ "GPSR2_83", 0x40E00020, 19, 0x00000001, 'd', "GPIO 83 set" },
{ "GPSR2_84", 0x40E00020, 20, 0x00000001, 'd', "GPIO 84 set" },

{ "GPCR0",    0x40E00024,  0, 0xffffffff, 'x', "GPIO Clear Register 0 (4-11)" },
{ "GPCR0_0",  0x40E00024,  0, 0x00000001, 'd', "GPIO 0 clear" },
{ "GPCR0_1",  0x40E00024,  1, 0x00000001, 'd', "GPIO 1 clear" },
{ "GPCR0_2",  0x40E00024,  2, 0x00000001, 'd', "GPIO 2 clear" },
{ "GPCR0_3",  0x40E00024,  3, 0x00000001, 'd', "GPIO 3 clear" },
{ "GPCR0_4",  0x40E00024,  4, 0x00000001, 'd', "GPIO 4 clear" },
{ "GPCR0_5",  0x40E00024,  5, 0x00000001, 'd', "GPIO 5 clear" },
{ "GPCR0_6",  0x40E00024,  6, 0x00000001, 'd', "GPIO 6 clear" },
{ "GPCR0_7",  0x40E00024,  7, 0x00000001, 'd', "GPIO 7 clear" },
{ "GPCR0_8",  0x40E00024,  8, 0x00000001, 'd', "GPIO 8 clear" },
{ "GPCR0_9",  0x40E00024,  9, 0x00000001, 'd', "GPIO 9 clear" },
{ "GPCR0_10", 0x40E00024, 10, 0x00000001, 'd', "GPIO 10 clear" },
{ "GPCR0_11", 0x40E00024, 11, 0x00000001, 'd', "GPIO 11 clear" },
{ "GPCR0_12", 0x40E00024, 12, 0x00000001, 'd', "GPIO 12 clear" },
{ "GPCR0_13", 0x40E00024, 13, 0x00000001, 'd', "GPIO 13 clear" },
{ "GPCR0_14", 0x40E00024, 14, 0x00000001, 'd', "GPIO 14 clear" },
{ "GPCR0_15", 0x40E00024, 15, 0x00000001, 'd', "GPIO 15 clear" },
{ "GPCR0_16", 0x40E00024, 16, 0x00000001, 'd', "GPIO 16 clear" },
{ "GPCR0_17", 0x40E00024, 17, 0x00000001, 'd', "GPIO 17 clear" },
{ "GPCR0_18", 0x40E00024, 18, 0x00000001, 'd', "GPIO 18 clear" },
{ "GPCR0_19", 0x40E00024, 19, 0x00000001, 'd', "GPIO 19 clear" },
{ "GPCR0_20", 0x40E00024, 20, 0x00000001, 'd', "GPIO 20 clear" },
{ "GPCR0_21", 0x40E00024, 21, 0x00000001, 'd', "GPIO 21 clear" },
{ "GPCR0_22", 0x40E00024, 22, 0x00000001, 'd', "GPIO 22 clear" },
{ "GPCR0_23", 0x40E00024, 23, 0x00000001, 'd', "GPIO 23 clear" },
{ "GPCR0_24", 0x40E00024, 24, 0x00000001, 'd', "GPIO 24 clear" },
{ "GPCR0_25", 0x40E00024, 25, 0x00000001, 'd', "GPIO 25 clear" },
{ "GPCR0_26", 0x40E00024, 26, 0x00000001, 'd', "GPIO 26 clear" },
{ "GPCR0_27", 0x40E00024, 27, 0x00000001, 'd', "GPIO 27 clear" },
{ "GPCR0_28", 0x40E00024, 28, 0x00000001, 'd', "GPIO 28 clear" },
{ "GPCR0_29", 0x40E00024, 29, 0x00000001, 'd', "GPIO 29 clear" },
{ "GPCR0_30", 0x40E00024, 30, 0x00000001, 'd', "GPIO 30 clear" },
{ "GPCR0_31", 0x40E00024, 31, 0x00000001, 'd', "GPIO 31 clear" },

{ "GPCR1",    0x40E00028,  0, 0xffffffff, 'x', "GPIO Clear Register 1 (4-11)" },
{ "GPCR1_32", 0x40E00028,  0, 0x00000001, 'd', "GPIO 32 clear" },
{ "GPCR1_33", 0x40E00028,  1, 0x00000001, 'd', "GPIO 33 clear" },
{ "GPCR1_34", 0x40E00028,  2, 0x00000001, 'd', "GPIO 34 clear" },
{ "GPCR1_35", 0x40E00028,  3, 0x00000001, 'd', "GPIO 35 clear" },
{ "GPCR1_36", 0x40E00028,  4, 0x00000001, 'd', "GPIO 36 clear" },
{ "GPCR1_37", 0x40E00028,  5, 0x00000001, 'd', "GPIO 37 clear" },
{ "GPCR1_38", 0x40E00028,  6, 0x00000001, 'd', "GPIO 38 clear" },
{ "GPCR1_39", 0x40E00028,  7, 0x00000001, 'd', "GPIO 39 clear" },
{ "GPCR1_40", 0x40E00028,  8, 0x00000001, 'd', "GPIO 40 clear" },
{ "GPCR1_41", 0x40E00028,  9, 0x00000001, 'd', "GPIO 41 clear" },
{ "GPCR1_42", 0x40E00028, 10, 0x00000001, 'd', "GPIO 42 clear" },
{ "GPCR1_43", 0x40E00028, 11, 0x00000001, 'd', "GPIO 43 clear" },
{ "GPCR1_44", 0x40E00028, 12, 0x00000001, 'd', "GPIO 44 clear" },
{ "GPCR1_45", 0x40E00028, 13, 0x00000001, 'd', "GPIO 45 clear" },
{ "GPCR1_46", 0x40E00028, 14, 0x00000001, 'd', "GPIO 46 clear" },
{ "GPCR1_47", 0x40E00028, 15, 0x00000001, 'd', "GPIO 47 clear" },
{ "GPCR1_48", 0x40E00028, 16, 0x00000001, 'd', "GPIO 48 clear" },
{ "GPCR1_49", 0x40E00028, 17, 0x00000001, 'd', "GPIO 49 clear" },
{ "GPCR1_50", 0x40E00028, 18, 0x00000001, 'd', "GPIO 50 clear" },
{ "GPCR1_51", 0x40E00028, 19, 0x00000001, 'd', "GPIO 51 clear" },
{ "GPCR1_52", 0x40E00028, 20, 0x00000001, 'd', "GPIO 52 clear" },
{ "GPCR1_53", 0x40E00028, 21, 0x00000001, 'd', "GPIO 53 clear" },
{ "GPCR1_54", 0x40E00028, 22, 0x00000001, 'd', "GPIO 54 clear" },
{ "GPCR1_55", 0x40E00028, 23, 0x00000001, 'd', "GPIO 55 clear" },
{ "GPCR1_56", 0x40E00028, 24, 0x00000001, 'd', "GPIO 56 clear" },
{ "GPCR1_57", 0x40E00028, 25, 0x00000001, 'd', "GPIO 57 clear" },
{ "GPCR1_58", 0x40E00028, 26, 0x00000001, 'd', "GPIO 58 clear" },
{ "GPCR1_59", 0x40E00028, 27, 0x00000001, 'd', "GPIO 59 clear" },
{ "GPCR1_60", 0x40E00028, 28, 0x00000001, 'd', "GPIO 60 clear" },
{ "GPCR1_61", 0x40E00028, 29, 0x00000001, 'd', "GPIO 61 clear" },
{ "GPCR1_62", 0x40E00028, 30, 0x00000001, 'd', "GPIO 62 clear" },
{ "GPCR1_63", 0x40E00028, 31, 0x00000001, 'd', "GPIO 63 clear" },

{ "GPCR2",    0x40E0002C,  0, 0xffffffff, 'x', "GPIO Clear Register 2 (4-12)" },
{ "GPCR2_64", 0x40E0002C,  0, 0x00000001, 'd', "GPIO 64 clear" },
{ "GPCR2_65", 0x40E0002C,  1, 0x00000001, 'd', "GPIO 65 clear" },
{ "GPCR2_66", 0x40E0002C,  2, 0x00000001, 'd', "GPIO 66 clear" },
{ "GPCR2_67", 0x40E0002C,  3, 0x00000001, 'd', "GPIO 67 clear" },
{ "GPCR2_68", 0x40E0002C,  4, 0x00000001, 'd', "GPIO 68 clear" },
{ "GPCR2_69", 0x40E0002C,  5, 0x00000001, 'd', "GPIO 69 clear" },
{ "GPCR2_70", 0x40E0002C,  6, 0x00000001, 'd', "GPIO 70 clear" },
{ "GPCR2_71", 0x40E0002C,  7, 0x00000001, 'd', "GPIO 71 clear" },
{ "GPCR2_72", 0x40E0002C,  8, 0x00000001, 'd', "GPIO 72 clear" },
{ "GPCR2_73", 0x40E0002C,  9, 0x00000001, 'd', "GPIO 73 clear" },
{ "GPCR2_74", 0x40E0002C, 10, 0x00000001, 'd', "GPIO 74 clear" },
{ "GPCR2_75", 0x40E0002C, 11, 0x00000001, 'd', "GPIO 75 clear" },
{ "GPCR2_76", 0x40E0002C, 12, 0x00000001, 'd', "GPIO 76 clear" },
{ "GPCR2_77", 0x40E0002C, 13, 0x00000001, 'd', "GPIO 77 clear" },
{ "GPCR2_78", 0x40E0002C, 14, 0x00000001, 'd', "GPIO 78 clear" },
{ "GPCR2_79", 0x40E0002C, 15, 0x00000001, 'd', "GPIO 79 clear" },
{ "GPCR2_80", 0x40E0002C, 16, 0x00000001, 'd', "GPIO 80 clear" },
{ "GPCR2_81", 0x40E0002C, 17, 0x00000001, 'd', "GPIO 81 clear" },
{ "GPCR2_82", 0x40E0002C, 18, 0x00000001, 'd', "GPIO 82 clear" },
{ "GPCR2_83", 0x40E0002C, 19, 0x00000001, 'd', "GPIO 83 clear" },
{ "GPCR2_84", 0x40E0002C, 20, 0x00000001, 'd', "GPIO 84 clear" },

{ "GRER0",    0x40E00030,  0, 0xffffffff, 'x', "GPIO Raising Edge Detect Enable Register 0 (4-13)" },
{ "GRER0_0",  0x40E00030,  0, 0x00000001, 'd', "GPIO 0 raising edge detect enabled" },
{ "GRER0_1",  0x40E00030,  1, 0x00000001, 'd', "GPIO 1 raising edge detect enabled" },
{ "GRER0_2",  0x40E00030,  2, 0x00000001, 'd', "GPIO 2 raising edge detect enabled" },
{ "GRER0_3",  0x40E00030,  3, 0x00000001, 'd', "GPIO 3 raising edge detect enabled" },
{ "GRER0_4",  0x40E00030,  4, 0x00000001, 'd', "GPIO 4 raising edge detect enabled" },
{ "GRER0_5",  0x40E00030,  5, 0x00000001, 'd', "GPIO 5 raising edge detect enabled" },
{ "GRER0_6",  0x40E00030,  6, 0x00000001, 'd', "GPIO 6 raising edge detect enabled" },
{ "GRER0_7",  0x40E00030,  7, 0x00000001, 'd', "GPIO 7 raising edge detect enabled" },
{ "GRER0_8",  0x40E00030,  8, 0x00000001, 'd', "GPIO 8 raising edge detect enabled" },
{ "GRER0_9",  0x40E00030,  9, 0x00000001, 'd', "GPIO 9 raising edge detect enabled" },
{ "GRER0_10", 0x40E00030, 10, 0x00000001, 'd', "GPIO 10 raising edge detect enabled" },
{ "GRER0_11", 0x40E00030, 11, 0x00000001, 'd', "GPIO 11 raising edge detect enabled" },
{ "GRER0_12", 0x40E00030, 12, 0x00000001, 'd', "GPIO 12 raising edge detect enabled" },
{ "GRER0_13", 0x40E00030, 13, 0x00000001, 'd', "GPIO 13 raising edge detect enabled" },
{ "GRER0_14", 0x40E00030, 14, 0x00000001, 'd', "GPIO 14 raising edge detect enabled" },
{ "GRER0_15", 0x40E00030, 15, 0x00000001, 'd', "GPIO 15 raising edge detect enabled" },
{ "GRER0_16", 0x40E00030, 16, 0x00000001, 'd', "GPIO 16 raising edge detect enabled" },
{ "GRER0_17", 0x40E00030, 17, 0x00000001, 'd', "GPIO 17 raising edge detect enabled" },
{ "GRER0_18", 0x40E00030, 18, 0x00000001, 'd', "GPIO 18 raising edge detect enabled" },
{ "GRER0_19", 0x40E00030, 19, 0x00000001, 'd', "GPIO 19 raising edge detect enabled" },
{ "GRER0_20", 0x40E00030, 20, 0x00000001, 'd', "GPIO 20 raising edge detect enabled" },
{ "GRER0_21", 0x40E00030, 21, 0x00000001, 'd', "GPIO 21 raising edge detect enabled" },
{ "GRER0_22", 0x40E00030, 22, 0x00000001, 'd', "GPIO 22 raising edge detect enabled" },
{ "GRER0_23", 0x40E00030, 23, 0x00000001, 'd', "GPIO 23 raising edge detect enabled" },
{ "GRER0_24", 0x40E00030, 24, 0x00000001, 'd', "GPIO 24 raising edge detect enabled" },
{ "GRER0_25", 0x40E00030, 25, 0x00000001, 'd', "GPIO 25 raising edge detect enabled" },
{ "GRER0_26", 0x40E00030, 26, 0x00000001, 'd', "GPIO 26 raising edge detect enabled" },
{ "GRER0_27", 0x40E00030, 27, 0x00000001, 'd', "GPIO 27 raising edge detect enabled" },
{ "GRER0_28", 0x40E00030, 28, 0x00000001, 'd', "GPIO 28 raising edge detect enabled" },
{ "GRER0_29", 0x40E00030, 29, 0x00000001, 'd', "GPIO 29 raising edge detect enabled" },
{ "GRER0_30", 0x40E00030, 30, 0x00000001, 'd', "GPIO 30 raising edge detect enabled" },
{ "GRER0_31", 0x40E00030, 31, 0x00000001, 'd', "GPIO 31 raising edge detect enabled" },

{ "GRER1",    0x40E00034,  0, 0xffffffff, 'x', "GPIO Raising Edge Detect Enable Register 1 (4-13)" },
{ "GRER1_32", 0x40E00034,  0, 0x00000001, 'd', "GPIO 32 raising edge detect enabled" },
{ "GRER1_33", 0x40E00034,  1, 0x00000001, 'd', "GPIO 33 raising edge detect enabled" },
{ "GRER1_34", 0x40E00034,  2, 0x00000001, 'd', "GPIO 34 raising edge detect enabled" },
{ "GRER1_35", 0x40E00034,  3, 0x00000001, 'd', "GPIO 35 raising edge detect enabled" },
{ "GRER1_36", 0x40E00034,  4, 0x00000001, 'd', "GPIO 36 raising edge detect enabled" },
{ "GRER1_37", 0x40E00034,  5, 0x00000001, 'd', "GPIO 37 raising edge detect enabled" },
{ "GRER1_38", 0x40E00034,  6, 0x00000001, 'd', "GPIO 38 raising edge detect enabled" },
{ "GRER1_39", 0x40E00034,  7, 0x00000001, 'd', "GPIO 39 raising edge detect enabled" },
{ "GRER1_40", 0x40E00034,  8, 0x00000001, 'd', "GPIO 40 raising edge detect enabled" },
{ "GRER1_41", 0x40E00034,  9, 0x00000001, 'd', "GPIO 41 raising edge detect enabled" },
{ "GRER1_42", 0x40E00034, 10, 0x00000001, 'd', "GPIO 42 raising edge detect enabled" },
{ "GRER1_43", 0x40E00034, 11, 0x00000001, 'd', "GPIO 43 raising edge detect enabled" },
{ "GRER1_44", 0x40E00034, 12, 0x00000001, 'd', "GPIO 44 raising edge detect enabled" },
{ "GRER1_45", 0x40E00034, 13, 0x00000001, 'd', "GPIO 45 raising edge detect enabled" },
{ "GRER1_46", 0x40E00034, 14, 0x00000001, 'd', "GPIO 46 raising edge detect enabled" },
{ "GRER1_47", 0x40E00034, 15, 0x00000001, 'd', "GPIO 47 raising edge detect enabled" },
{ "GRER1_48", 0x40E00034, 16, 0x00000001, 'd', "GPIO 48 raising edge detect enabled" },
{ "GRER1_49", 0x40E00034, 17, 0x00000001, 'd', "GPIO 49 raising edge detect enabled" },
{ "GRER1_50", 0x40E00034, 18, 0x00000001, 'd', "GPIO 50 raising edge detect enabled" },
{ "GRER1_51", 0x40E00034, 19, 0x00000001, 'd', "GPIO 51 raising edge detect enabled" },
{ "GRER1_52", 0x40E00034, 20, 0x00000001, 'd', "GPIO 52 raising edge detect enabled" },
{ "GRER1_53", 0x40E00034, 21, 0x00000001, 'd', "GPIO 53 raising edge detect enabled" },
{ "GRER1_54", 0x40E00034, 22, 0x00000001, 'd', "GPIO 54 raising edge detect enabled" },
{ "GRER1_55", 0x40E00034, 23, 0x00000001, 'd', "GPIO 55 raising edge detect enabled" },
{ "GRER1_56", 0x40E00034, 24, 0x00000001, 'd', "GPIO 56 raising edge detect enabled" },
{ "GRER1_57", 0x40E00034, 25, 0x00000001, 'd', "GPIO 57 raising edge detect enabled" },
{ "GRER1_58", 0x40E00034, 26, 0x00000001, 'd', "GPIO 58 raising edge detect enabled" },
{ "GRER1_59", 0x40E00034, 27, 0x00000001, 'd', "GPIO 59 raising edge detect enabled" },
{ "GRER1_60", 0x40E00034, 28, 0x00000001, 'd', "GPIO 60 raising edge detect enabled" },
{ "GRER1_61", 0x40E00034, 29, 0x00000001, 'd', "GPIO 61 raising edge detect enabled" },
{ "GRER1_62", 0x40E00034, 30, 0x00000001, 'd', "GPIO 62 raising edge detect enabled" },
{ "GRER1_63", 0x40E00034, 31, 0x00000001, 'd', "GPIO 63 raising edge detect enabled" },

{ "GRER2",    0x40E00038,  0, 0xffffffff, 'x', "GPIO Raising Edge Detect Enable Register 2 (4-13)" },
{ "GRER2_64", 0x40E00038,  0, 0x00000001, 'd', "GPIO 64 raising edge detect enabled" },
{ "GRER2_65", 0x40E00038,  1, 0x00000001, 'd', "GPIO 65 raising edge detect enabled" },
{ "GRER2_66", 0x40E00038,  2, 0x00000001, 'd', "GPIO 66 raising edge detect enabled" },
{ "GRER2_67", 0x40E00038,  3, 0x00000001, 'd', "GPIO 67 raising edge detect enabled" },
{ "GRER2_68", 0x40E00038,  4, 0x00000001, 'd', "GPIO 68 raising edge detect enabled" },
{ "GRER2_69", 0x40E00038,  5, 0x00000001, 'd', "GPIO 69 raising edge detect enabled" },
{ "GRER2_70", 0x40E00038,  6, 0x00000001, 'd', "GPIO 70 raising edge detect enabled" },
{ "GRER2_71", 0x40E00038,  7, 0x00000001, 'd', "GPIO 71 raising edge detect enabled" },
{ "GRER2_72", 0x40E00038,  8, 0x00000001, 'd', "GPIO 72 raising edge detect enabled" },
{ "GRER2_73", 0x40E00038,  9, 0x00000001, 'd', "GPIO 73 raising edge detect enabled" },
{ "GRER2_74", 0x40E00038, 10, 0x00000001, 'd', "GPIO 74 raising edge detect enabled" },
{ "GRER2_75", 0x40E00038, 11, 0x00000001, 'd', "GPIO 75 raising edge detect enabled" },
{ "GRER2_76", 0x40E00038, 12, 0x00000001, 'd', "GPIO 76 raising edge detect enabled" },
{ "GRER2_77", 0x40E00038, 13, 0x00000001, 'd', "GPIO 77 raising edge detect enabled" },
{ "GRER2_78", 0x40E00038, 14, 0x00000001, 'd', "GPIO 78 raising edge detect enabled" },
{ "GRER2_79", 0x40E00038, 15, 0x00000001, 'd', "GPIO 79 raising edge detect enabled" },
{ "GRER2_80", 0x40E00038, 16, 0x00000001, 'd', "GPIO 80 raising edge detect enabled" },
{ "GRER2_81", 0x40E00038, 17, 0x00000001, 'd', "GPIO 81 raising edge detect enabled" },
{ "GRER2_82", 0x40E00038, 18, 0x00000001, 'd', "GPIO 82 raising edge detect enabled" },
{ "GRER2_83", 0x40E00038, 19, 0x00000001, 'd', "GPIO 83 raising edge detect enabled" },
{ "GRER2_84", 0x40E00038, 20, 0x00000001, 'd', "GPIO 84 raising edge detect enabled" },

{ "GFER0",    0x40E0003C,  0, 0xffffffff, 'x', "GPIO Falling Edge Detect Enable Register 0 (4-14)" },
{ "GFER0_0",  0x40E0003C,  0, 0x00000001, 'd', "GPIO 0 falling edge detect enabled" },
{ "GFER0_1",  0x40E0003C,  1, 0x00000001, 'd', "GPIO 1 falling edge detect enabled" },
{ "GFER0_2",  0x40E0003C,  2, 0x00000001, 'd', "GPIO 2 falling edge detect enabled" },
{ "GFER0_3",  0x40E0003C,  3, 0x00000001, 'd', "GPIO 3 falling edge detect enabled" },
{ "GFER0_4",  0x40E0003C,  4, 0x00000001, 'd', "GPIO 4 falling edge detect enabled" },
{ "GFER0_5",  0x40E0003C,  5, 0x00000001, 'd', "GPIO 5 falling edge detect enabled" },
{ "GFER0_6",  0x40E0003C,  6, 0x00000001, 'd', "GPIO 6 falling edge detect enabled" },
{ "GFER0_7",  0x40E0003C,  7, 0x00000001, 'd', "GPIO 7 falling edge detect enabled" },
{ "GFER0_8",  0x40E0003C,  8, 0x00000001, 'd', "GPIO 8 falling edge detect enabled" },
{ "GFER0_9",  0x40E0003C,  9, 0x00000001, 'd', "GPIO 9 falling edge detect enabled" },
{ "GFER0_10", 0x40E0003C, 10, 0x00000001, 'd', "GPIO 10 falling edge detect enabled" },
{ "GFER0_11", 0x40E0003C, 11, 0x00000001, 'd', "GPIO 11 falling edge detect enabled" },
{ "GFER0_12", 0x40E0003C, 12, 0x00000001, 'd', "GPIO 12 falling edge detect enabled" },
{ "GFER0_13", 0x40E0003C, 13, 0x00000001, 'd', "GPIO 13 falling edge detect enabled" },
{ "GFER0_14", 0x40E0003C, 14, 0x00000001, 'd', "GPIO 14 falling edge detect enabled" },
{ "GFER0_15", 0x40E0003C, 15, 0x00000001, 'd', "GPIO 15 falling edge detect enabled" },
{ "GFER0_16", 0x40E0003C, 16, 0x00000001, 'd', "GPIO 16 falling edge detect enabled" },
{ "GFER0_17", 0x40E0003C, 17, 0x00000001, 'd', "GPIO 17 falling edge detect enabled" },
{ "GFER0_18", 0x40E0003C, 18, 0x00000001, 'd', "GPIO 18 falling edge detect enabled" },
{ "GFER0_19", 0x40E0003C, 19, 0x00000001, 'd', "GPIO 19 falling edge detect enabled" },
{ "GFER0_20", 0x40E0003C, 20, 0x00000001, 'd', "GPIO 20 falling edge detect enabled" },
{ "GFER0_21", 0x40E0003C, 21, 0x00000001, 'd', "GPIO 21 falling edge detect enabled" },
{ "GFER0_22", 0x40E0003C, 22, 0x00000001, 'd', "GPIO 22 falling edge detect enabled" },
{ "GFER0_23", 0x40E0003C, 23, 0x00000001, 'd', "GPIO 23 falling edge detect enabled" },
{ "GFER0_24", 0x40E0003C, 24, 0x00000001, 'd', "GPIO 24 falling edge detect enabled" },
{ "GFER0_25", 0x40E0003C, 25, 0x00000001, 'd', "GPIO 25 falling edge detect enabled" },
{ "GFER0_26", 0x40E0003C, 26, 0x00000001, 'd', "GPIO 26 falling edge detect enabled" },
{ "GFER0_27", 0x40E0003C, 27, 0x00000001, 'd', "GPIO 27 falling edge detect enabled" },
{ "GFER0_28", 0x40E0003C, 28, 0x00000001, 'd', "GPIO 28 falling edge detect enabled" },
{ "GFER0_29", 0x40E0003C, 29, 0x00000001, 'd', "GPIO 29 falling edge detect enabled" },
{ "GFER0_30", 0x40E0003C, 30, 0x00000001, 'd', "GPIO 30 falling edge detect enabled" },
{ "GFER0_31", 0x40E0003C, 31, 0x00000001, 'd', "GPIO 31 falling edge detect enabled" },

{ "GFER1",    0x40E00040,  0, 0xffffffff, 'x', "GPIO Falling Edge Detect Enable Register 1 (4-14)" },
{ "GFER1_32", 0x40E00040,  0, 0x00000001, 'd', "GPIO 32 falling edge detect enabled" },
{ "GFER1_33", 0x40E00040,  1, 0x00000001, 'd', "GPIO 33 falling edge detect enabled" },
{ "GFER1_34", 0x40E00040,  2, 0x00000001, 'd', "GPIO 34 falling edge detect enabled" },
{ "GFER1_35", 0x40E00040,  3, 0x00000001, 'd', "GPIO 35 falling edge detect enabled" },
{ "GFER1_36", 0x40E00040,  4, 0x00000001, 'd', "GPIO 36 falling edge detect enabled" },
{ "GFER1_37", 0x40E00040,  5, 0x00000001, 'd', "GPIO 37 falling edge detect enabled" },
{ "GFER1_38", 0x40E00040,  6, 0x00000001, 'd', "GPIO 38 falling edge detect enabled" },
{ "GFER1_39", 0x40E00040,  7, 0x00000001, 'd', "GPIO 39 falling edge detect enabled" },
{ "GFER1_40", 0x40E00040,  8, 0x00000001, 'd', "GPIO 40 falling edge detect enabled" },
{ "GFER1_41", 0x40E00040,  9, 0x00000001, 'd', "GPIO 41 falling edge detect enabled" },
{ "GFER1_42", 0x40E00040, 10, 0x00000001, 'd', "GPIO 42 falling edge detect enabled" },
{ "GFER1_43", 0x40E00040, 11, 0x00000001, 'd', "GPIO 43 falling edge detect enabled" },
{ "GFER1_44", 0x40E00040, 12, 0x00000001, 'd', "GPIO 44 falling edge detect enabled" },
{ "GFER1_45", 0x40E00040, 13, 0x00000001, 'd', "GPIO 45 falling edge detect enabled" },
{ "GFER1_46", 0x40E00040, 14, 0x00000001, 'd', "GPIO 46 falling edge detect enabled" },
{ "GFER1_47", 0x40E00040, 15, 0x00000001, 'd', "GPIO 47 falling edge detect enabled" },
{ "GFER1_48", 0x40E00040, 16, 0x00000001, 'd', "GPIO 48 falling edge detect enabled" },
{ "GFER1_49", 0x40E00040, 17, 0x00000001, 'd', "GPIO 49 falling edge detect enabled" },
{ "GFER1_50", 0x40E00040, 18, 0x00000001, 'd', "GPIO 50 falling edge detect enabled" },
{ "GFER1_51", 0x40E00040, 19, 0x00000001, 'd', "GPIO 51 falling edge detect enabled" },
{ "GFER1_52", 0x40E00040, 20, 0x00000001, 'd', "GPIO 52 falling edge detect enabled" },
{ "GFER1_53", 0x40E00040, 21, 0x00000001, 'd', "GPIO 53 falling edge detect enabled" },
{ "GFER1_54", 0x40E00040, 22, 0x00000001, 'd', "GPIO 54 falling edge detect enabled" },
{ "GFER1_55", 0x40E00040, 23, 0x00000001, 'd', "GPIO 55 falling edge detect enabled" },
{ "GFER1_56", 0x40E00040, 24, 0x00000001, 'd', "GPIO 56 falling edge detect enabled" },
{ "GFER1_57", 0x40E00040, 25, 0x00000001, 'd', "GPIO 57 falling edge detect enabled" },
{ "GFER1_58", 0x40E00040, 26, 0x00000001, 'd', "GPIO 58 falling edge detect enabled" },
{ "GFER1_59", 0x40E00040, 27, 0x00000001, 'd', "GPIO 59 falling edge detect enabled" },
{ "GFER1_60", 0x40E00040, 28, 0x00000001, 'd', "GPIO 60 falling edge detect enabled" },
{ "GFER1_61", 0x40E00040, 29, 0x00000001, 'd', "GPIO 61 falling edge detect enabled" },
{ "GFER1_62", 0x40E00040, 30, 0x00000001, 'd', "GPIO 62 falling edge detect enabled" },
{ "GFER1_63", 0x40E00040, 31, 0x00000001, 'd', "GPIO 63 falling edge detect enabled" },

{ "GFER2",    0x40E00044,  0, 0xffffffff, 'x', "GPIO Falling Edge Detect Enable Register 2 (4-14)" },
{ "GFER2_64", 0x40E00044,  0, 0x00000001, 'd', "GPIO 64 falling edge detect enabled" },
{ "GFER2_65", 0x40E00044,  1, 0x00000001, 'd', "GPIO 65 falling edge detect enabled" },
{ "GFER2_66", 0x40E00044,  2, 0x00000001, 'd', "GPIO 66 falling edge detect enabled" },
{ "GFER2_67", 0x40E00044,  3, 0x00000001, 'd', "GPIO 67 falling edge detect enabled" },
{ "GFER2_68", 0x40E00044,  4, 0x00000001, 'd', "GPIO 68 falling edge detect enabled" },
{ "GFER2_69", 0x40E00044,  5, 0x00000001, 'd', "GPIO 69 falling edge detect enabled" },
{ "GFER2_70", 0x40E00044,  6, 0x00000001, 'd', "GPIO 70 falling edge detect enabled" },
{ "GFER2_71", 0x40E00044,  7, 0x00000001, 'd', "GPIO 71 falling edge detect enabled" },
{ "GFER2_72", 0x40E00044,  8, 0x00000001, 'd', "GPIO 72 falling edge detect enabled" },
{ "GFER2_73", 0x40E00044,  9, 0x00000001, 'd', "GPIO 73 falling edge detect enabled" },
{ "GFER2_74", 0x40E00044, 10, 0x00000001, 'd', "GPIO 74 falling edge detect enabled" },
{ "GFER2_75", 0x40E00044, 11, 0x00000001, 'd', "GPIO 75 falling edge detect enabled" },
{ "GFER2_76", 0x40E00044, 12, 0x00000001, 'd', "GPIO 76 falling edge detect enabled" },
{ "GFER2_77", 0x40E00044, 13, 0x00000001, 'd', "GPIO 77 falling edge detect enabled" },
{ "GFER2_78", 0x40E00044, 14, 0x00000001, 'd', "GPIO 78 falling edge detect enabled" },
{ "GFER2_79", 0x40E00044, 15, 0x00000001, 'd', "GPIO 79 falling edge detect enabled" },
{ "GFER2_80", 0x40E00044, 16, 0x00000001, 'd', "GPIO 80 falling edge detect enabled" },
{ "GFER2_81", 0x40E00044, 17, 0x00000001, 'd', "GPIO 81 falling edge detect enabled" },
{ "GFER2_82", 0x40E00044, 18, 0x00000001, 'd', "GPIO 82 falling edge detect enabled" },
{ "GFER2_83", 0x40E00044, 19, 0x00000001, 'd', "GPIO 83 falling edge detect enabled" },
{ "GFER2_84", 0x40E00044, 20, 0x00000001, 'd', "GPIO 84 falling edge detect enabled" },

{ "GEDR0",    0x40E00048,  0, 0xffffffff, 'x', "GPIO Edge Detect Register 0 (4-15)" },
{ "GEDR0_0",  0x40E00048,  0, 0x00000001, 'd', "GPIO 0 edge detected" },
{ "GEDR0_1",  0x40E00048,  1, 0x00000001, 'd', "GPIO 1 edge detected" },
{ "GEDR0_2",  0x40E00048,  2, 0x00000001, 'd', "GPIO 2 edge detected" },
{ "GEDR0_3",  0x40E00048,  3, 0x00000001, 'd', "GPIO 3 edge detected" },
{ "GEDR0_4",  0x40E00048,  4, 0x00000001, 'd', "GPIO 4 edge detected" },
{ "GEDR0_5",  0x40E00048,  5, 0x00000001, 'd', "GPIO 5 edge detected" },
{ "GEDR0_6",  0x40E00048,  6, 0x00000001, 'd', "GPIO 6 edge detected" },
{ "GEDR0_7",  0x40E00048,  7, 0x00000001, 'd', "GPIO 7 edge detected" },
{ "GEDR0_8",  0x40E00048,  8, 0x00000001, 'd', "GPIO 8 edge detected" },
{ "GEDR0_9",  0x40E00048,  9, 0x00000001, 'd', "GPIO 9 edge detected" },
{ "GEDR0_10", 0x40E00048, 10, 0x00000001, 'd', "GPIO 10 edge detected" },
{ "GEDR0_11", 0x40E00048, 11, 0x00000001, 'd', "GPIO 11 edge detected" },
{ "GEDR0_12", 0x40E00048, 12, 0x00000001, 'd', "GPIO 12 edge detected" },
{ "GEDR0_13", 0x40E00048, 13, 0x00000001, 'd', "GPIO 13 edge detected" },
{ "GEDR0_14", 0x40E00048, 14, 0x00000001, 'd', "GPIO 14 edge detected" },
{ "GEDR0_15", 0x40E00048, 15, 0x00000001, 'd', "GPIO 15 edge detected" },
{ "GEDR0_16", 0x40E00048, 16, 0x00000001, 'd', "GPIO 16 edge detected" },
{ "GEDR0_17", 0x40E00048, 17, 0x00000001, 'd', "GPIO 17 edge detected" },
{ "GEDR0_18", 0x40E00048, 18, 0x00000001, 'd', "GPIO 18 edge detected" },
{ "GEDR0_19", 0x40E00048, 19, 0x00000001, 'd', "GPIO 19 edge detected" },
{ "GEDR0_20", 0x40E00048, 20, 0x00000001, 'd', "GPIO 20 edge detected" },
{ "GEDR0_21", 0x40E00048, 21, 0x00000001, 'd', "GPIO 21 edge detected" },
{ "GEDR0_22", 0x40E00048, 22, 0x00000001, 'd', "GPIO 22 edge detected" },
{ "GEDR0_23", 0x40E00048, 23, 0x00000001, 'd', "GPIO 23 edge detected" },
{ "GEDR0_24", 0x40E00048, 24, 0x00000001, 'd', "GPIO 24 edge detected" },
{ "GEDR0_25", 0x40E00048, 25, 0x00000001, 'd', "GPIO 25 edge detected" },
{ "GEDR0_26", 0x40E00048, 26, 0x00000001, 'd', "GPIO 26 edge detected" },
{ "GEDR0_27", 0x40E00048, 27, 0x00000001, 'd', "GPIO 27 edge detected" },
{ "GEDR0_28", 0x40E00048, 28, 0x00000001, 'd', "GPIO 28 edge detected" },
{ "GEDR0_29", 0x40E00048, 29, 0x00000001, 'd', "GPIO 29 edge detected" },
{ "GEDR0_30", 0x40E00048, 30, 0x00000001, 'd', "GPIO 30 edge detected" },
{ "GEDR0_31", 0x40E00048, 31, 0x00000001, 'd', "GPIO 31 edge detected" },

{ "GEDR1",    0x40E0004C,  0, 0xffffffff, 'x', "GPIO Edge Detect Register 1 (4-16)" },
{ "GEDR1_32", 0x40E0004C,  0, 0x00000001, 'd', "GPIO 32 edge detected" },
{ "GEDR1_33", 0x40E0004C,  1, 0x00000001, 'd', "GPIO 33 edge detected" },
{ "GEDR1_34", 0x40E0004C,  2, 0x00000001, 'd', "GPIO 34 edge detected" },
{ "GEDR1_35", 0x40E0004C,  3, 0x00000001, 'd', "GPIO 35 edge detected" },
{ "GEDR1_36", 0x40E0004C,  4, 0x00000001, 'd', "GPIO 36 edge detected" },
{ "GEDR1_37", 0x40E0004C,  5, 0x00000001, 'd', "GPIO 37 edge detected" },
{ "GEDR1_38", 0x40E0004C,  6, 0x00000001, 'd', "GPIO 38 edge detected" },
{ "GEDR1_39", 0x40E0004C,  7, 0x00000001, 'd', "GPIO 39 edge detected" },
{ "GEDR1_40", 0x40E0004C,  8, 0x00000001, 'd', "GPIO 40 edge detected" },
{ "GEDR1_41", 0x40E0004C,  9, 0x00000001, 'd', "GPIO 41 edge detected" },
{ "GEDR1_42", 0x40E0004C, 10, 0x00000001, 'd', "GPIO 42 edge detected" },
{ "GEDR1_43", 0x40E0004C, 11, 0x00000001, 'd', "GPIO 43 edge detected" },
{ "GEDR1_44", 0x40E0004C, 12, 0x00000001, 'd', "GPIO 44 edge detected" },
{ "GEDR1_45", 0x40E0004C, 13, 0x00000001, 'd', "GPIO 45 edge detected" },
{ "GEDR1_46", 0x40E0004C, 14, 0x00000001, 'd', "GPIO 46 edge detected" },
{ "GEDR1_47", 0x40E0004C, 15, 0x00000001, 'd', "GPIO 47 edge detected" },
{ "GEDR1_48", 0x40E0004C, 16, 0x00000001, 'd', "GPIO 48 edge detected" },
{ "GEDR1_49", 0x40E0004C, 17, 0x00000001, 'd', "GPIO 49 edge detected" },
{ "GEDR1_50", 0x40E0004C, 18, 0x00000001, 'd', "GPIO 50 edge detected" },
{ "GEDR1_51", 0x40E0004C, 19, 0x00000001, 'd', "GPIO 51 edge detected" },
{ "GEDR1_52", 0x40E0004C, 20, 0x00000001, 'd', "GPIO 52 edge detected" },
{ "GEDR1_53", 0x40E0004C, 21, 0x00000001, 'd', "GPIO 53 edge detected" },
{ "GEDR1_54", 0x40E0004C, 22, 0x00000001, 'd', "GPIO 54 edge detected" },
{ "GEDR1_55", 0x40E0004C, 23, 0x00000001, 'd', "GPIO 55 edge detected" },
{ "GEDR1_56", 0x40E0004C, 24, 0x00000001, 'd', "GPIO 56 edge detected" },
{ "GEDR1_57", 0x40E0004C, 25, 0x00000001, 'd', "GPIO 57 edge detected" },
{ "GEDR1_58", 0x40E0004C, 26, 0x00000001, 'd', "GPIO 58 edge detected" },
{ "GEDR1_59", 0x40E0004C, 27, 0x00000001, 'd', "GPIO 59 edge detected" },
{ "GEDR1_60", 0x40E0004C, 28, 0x00000001, 'd', "GPIO 60 edge detected" },
{ "GEDR1_61", 0x40E0004C, 29, 0x00000001, 'd', "GPIO 61 edge detected" },
{ "GEDR1_62", 0x40E0004C, 30, 0x00000001, 'd', "GPIO 62 edge detected" },
{ "GEDR1_63", 0x40E0004C, 31, 0x00000001, 'd', "GPIO 63 edge detected" },

{ "GEDR2",    0x40E00050,  0, 0xffffffff, 'x', "GPIO Edge Detect Register 2 (4-16)" },
{ "GEDR2_64", 0x40E00050,  0, 0x00000001, 'd', "GPIO 64 edge detected" },
{ "GEDR2_65", 0x40E00050,  1, 0x00000001, 'd', "GPIO 65 edge detected" },
{ "GEDR2_66", 0x40E00050,  2, 0x00000001, 'd', "GPIO 66 edge detected" },
{ "GEDR2_67", 0x40E00050,  3, 0x00000001, 'd', "GPIO 67 edge detected" },
{ "GEDR2_68", 0x40E00050,  4, 0x00000001, 'd', "GPIO 68 edge detected" },
{ "GEDR2_69", 0x40E00050,  5, 0x00000001, 'd', "GPIO 69 edge detected" },
{ "GEDR2_70", 0x40E00050,  6, 0x00000001, 'd', "GPIO 70 edge detected" },
{ "GEDR2_71", 0x40E00050,  7, 0x00000001, 'd', "GPIO 71 edge detected" },
{ "GEDR2_72", 0x40E00050,  8, 0x00000001, 'd', "GPIO 72 edge detected" },
{ "GEDR2_73", 0x40E00050,  9, 0x00000001, 'd', "GPIO 73 edge detected" },
{ "GEDR2_74", 0x40E00050, 10, 0x00000001, 'd', "GPIO 74 edge detected" },
{ "GEDR2_75", 0x40E00050, 11, 0x00000001, 'd', "GPIO 75 edge detected" },
{ "GEDR2_76", 0x40E00050, 12, 0x00000001, 'd', "GPIO 76 edge detected" },
{ "GEDR2_77", 0x40E00050, 13, 0x00000001, 'd', "GPIO 77 edge detected" },
{ "GEDR2_78", 0x40E00050, 14, 0x00000001, 'd', "GPIO 78 edge detected" },
{ "GEDR2_79", 0x40E00050, 15, 0x00000001, 'd', "GPIO 79 edge detected" },
{ "GEDR2_80", 0x40E00050, 16, 0x00000001, 'd', "GPIO 80 edge detected" },
{ "GEDR2_81", 0x40E00050, 17, 0x00000001, 'd', "GPIO 81 edge detected" },
{ "GEDR2_82", 0x40E00050, 18, 0x00000001, 'd', "GPIO 82 edge detected" },
{ "GEDR2_83", 0x40E00050, 19, 0x00000001, 'd', "GPIO 83 edge detected" },
{ "GEDR2_84", 0x40E00050, 20, 0x00000001, 'd', "GPIO 84 edge detected" },

{ "GAFR0L",    0x40E00054,  0, 0xffffffff, 'x', "GPIO Alternate Function Register 0 Lower (4-17)" },
{ "GAFR0L_0",  0x40E00054,  0, 0x00000003, 'x', "GPIO 0 alternate function select" },
{ "GAFR0L_1",  0x40E00054,  2, 0x00000003, 'x', "GPIO 1 alternate function select" },
{ "GAFR0L_2",  0x40E00054,  4, 0x00000003, 'x', "GPIO 2 alternate function select" },
{ "GAFR0L_3",  0x40E00054,  6, 0x00000003, 'x', "GPIO 3 alternate function select" },
{ "GAFR0L_4",  0x40E00054,  8, 0x00000003, 'x', "GPIO 4 alternate function select" },
{ "GAFR0L_5",  0x40E00054, 10, 0x00000003, 'x', "GPIO 5 alternate function select" },
{ "GAFR0L_6",  0x40E00054, 12, 0x00000003, 'x', "GPIO 6 alternate function select" },
{ "GAFR0L_7",  0x40E00054, 14, 0x00000003, 'x', "GPIO 7 alternate function select" },
{ "GAFR0L_8",  0x40E00054, 16, 0x00000003, 'x', "GPIO 8 alternate function select" },
{ "GAFR0L_9",  0x40E00054, 18, 0x00000003, 'x', "GPIO 9 alternate function select" },
{ "GAFR0L_10", 0x40E00054, 20, 0x00000003, 'x', "GPIO 10 alternate function select" },
{ "GAFR0L_11", 0x40E00054, 22, 0x00000003, 'x', "GPIO 11 alternate function select" },
{ "GAFR0L_12", 0x40E00054, 24, 0x00000003, 'x', "GPIO 12 alternate function select" },
{ "GAFR0L_13", 0x40E00054, 26, 0x00000003, 'x', "GPIO 13 alternate function select" },
{ "GAFR0L_14", 0x40E00054, 28, 0x00000003, 'x', "GPIO 14 alternate function select" },
{ "GAFR0L_15", 0x40E00054, 30, 0x00000003, 'x', "GPIO 15 alternate function select" },

{ "GAFR0U",    0x40E00058,  0, 0xffffffff, 'x', "GPIO Alternate Function Register 0 Upper (4-18)" },
{ "GAFR0U_16", 0x40E00058,  0, 0x00000003, 'x', "GPIO 16 alternate function select" },
{ "GAFR0U_17", 0x40E00058,  2, 0x00000003, 'x', "GPIO 17 alternate function select" },
{ "GAFR0U_18", 0x40E00058,  4, 0x00000003, 'x', "GPIO 18 alternate function select" },
{ "GAFR0U_19", 0x40E00058,  6, 0x00000003, 'x', "GPIO 19 alternate function select" },
{ "GAFR0U_20", 0x40E00058,  8, 0x00000003, 'x', "GPIO 20 alternate function select" },
{ "GAFR0U_21", 0x40E00058, 10, 0x00000003, 'x', "GPIO 21 alternate function select" },
{ "GAFR0U_22", 0x40E00058, 12, 0x00000003, 'x', "GPIO 22 alternate function select" },
{ "GAFR0U_23", 0x40E00058, 14, 0x00000003, 'x', "GPIO 23 alternate function select" },
{ "GAFR0U_24", 0x40E00058, 16, 0x00000003, 'x', "GPIO 24 alternate function select" },
{ "GAFR0U_25", 0x40E00058, 18, 0x00000003, 'x', "GPIO 25 alternate function select" },
{ "GAFR0U_26", 0x40E00058, 20, 0x00000003, 'x', "GPIO 26 alternate function select" },
{ "GAFR0U_27", 0x40E00058, 22, 0x00000003, 'x', "GPIO 27 alternate function select" },
{ "GAFR0U_28", 0x40E00058, 24, 0x00000003, 'x', "GPIO 28 alternate function select" },
{ "GAFR0U_29", 0x40E00058, 26, 0x00000003, 'x', "GPIO 29 alternate function select" },
{ "GAFR0U_30", 0x40E00058, 28, 0x00000003, 'x', "GPIO 30 alternate function select" },
{ "GAFR0U_31", 0x40E00058, 30, 0x00000003, 'x', "GPIO 31 alternate function select" },

{ "GAFR1L",    0x40E0005C,  0, 0xffffffff, 'x', "GPIO Alternate Function Register 1 Lower (4-18)" },
{ "GAFR1L_32", 0x40E0005C,  0, 0x00000003, 'x', "GPIO 32 alternate function select" },
{ "GAFR1L_33", 0x40E0005C,  2, 0x00000003, 'x', "GPIO 33 alternate function select" },
{ "GAFR1L_34", 0x40E0005C,  4, 0x00000003, 'x', "GPIO 34 alternate function select" },
{ "GAFR1L_35", 0x40E0005C,  6, 0x00000003, 'x', "GPIO 35 alternate function select" },
{ "GAFR1L_36", 0x40E0005C,  8, 0x00000003, 'x', "GPIO 36 alternate function select" },
{ "GAFR1L_37", 0x40E0005C, 10, 0x00000003, 'x', "GPIO 37 alternate function select" },
{ "GAFR1L_38", 0x40E0005C, 12, 0x00000003, 'x', "GPIO 38 alternate function select" },
{ "GAFR1L_39", 0x40E0005C, 14, 0x00000003, 'x', "GPIO 39 alternate function select" },
{ "GAFR1L_40", 0x40E0005C, 16, 0x00000003, 'x', "GPIO 40 alternate function select" },
{ "GAFR1L_41", 0x40E0005C, 18, 0x00000003, 'x', "GPIO 41 alternate function select" },
{ "GAFR1L_42", 0x40E0005C, 20, 0x00000003, 'x', "GPIO 42 alternate function select" },
{ "GAFR1L_43", 0x40E0005C, 22, 0x00000003, 'x', "GPIO 43 alternate function select" },
{ "GAFR1L_44", 0x40E0005C, 24, 0x00000003, 'x', "GPIO 44 alternate function select" },
{ "GAFR1L_45", 0x40E0005C, 26, 0x00000003, 'x', "GPIO 45 alternate function select" },
{ "GAFR1L_46", 0x40E0005C, 28, 0x00000003, 'x', "GPIO 46 alternate function select" },
{ "GAFR1L_47", 0x40E0005C, 30, 0x00000003, 'x', "GPIO 47 alternate function select" },

{ "GAFR1U",    0x40E00060,  0, 0xffffffff, 'x', "GPIO Alternate Function Register 1 Upper (4-19)" },
{ "GAFR1U_48", 0x40E00060,  0, 0x00000003, 'x', "GPIO 48 alternate function select" },
{ "GAFR1U_49", 0x40E00060,  2, 0x00000003, 'x', "GPIO 49 alternate function select" },
{ "GAFR1U_50", 0x40E00060,  4, 0x00000003, 'x', "GPIO 50 alternate function select" },
{ "GAFR1U_51", 0x40E00060,  6, 0x00000003, 'x', "GPIO 51 alternate function select" },
{ "GAFR1U_52", 0x40E00060,  8, 0x00000003, 'x', "GPIO 52 alternate function select" },
{ "GAFR1U_53", 0x40E00060, 10, 0x00000003, 'x', "GPIO 53 alternate function select" },
{ "GAFR1U_54", 0x40E00060, 12, 0x00000003, 'x', "GPIO 54 alternate function select" },
{ "GAFR1U_55", 0x40E00060, 14, 0x00000003, 'x', "GPIO 55 alternate function select" },
{ "GAFR1U_56", 0x40E00060, 16, 0x00000003, 'x', "GPIO 56 alternate function select" },
{ "GAFR1U_57", 0x40E00060, 18, 0x00000003, 'x', "GPIO 57 alternate function select" },
{ "GAFR1U_58", 0x40E00060, 20, 0x00000003, 'x', "GPIO 58 alternate function select" },
{ "GAFR1U_59", 0x40E00060, 22, 0x00000003, 'x', "GPIO 59 alternate function select" },
{ "GAFR1U_60", 0x40E00060, 24, 0x00000003, 'x', "GPIO 60 alternate function select" },
{ "GAFR1U_61", 0x40E00060, 26, 0x00000003, 'x', "GPIO 61 alternate function select" },
{ "GAFR1U_62", 0x40E00060, 28, 0x00000003, 'x', "GPIO 62 alternate function select" },
{ "GAFR1U_63", 0x40E00060, 30, 0x00000003, 'x', "GPIO 63 alternate function select" },

{ "GAFR2L",    0x40E00064,  0, 0xffffffff, 'x', "GPIO Alternate Function Register 2 Lower (4-19)" },
{ "GAFR2L_64", 0x40E00064,  0, 0x00000003, 'x', "GPIO 64 alternate function select" },
{ "GAFR2L_65", 0x40E00064,  2, 0x00000003, 'x', "GPIO 65 alternate function select" },
{ "GAFR2L_66", 0x40E00064,  4, 0x00000003, 'x', "GPIO 66 alternate function select" },
{ "GAFR2L_67", 0x40E00064,  6, 0x00000003, 'x', "GPIO 67 alternate function select" },
{ "GAFR2L_68", 0x40E00064,  8, 0x00000003, 'x', "GPIO 68 alternate function select" },
{ "GAFR2L_69", 0x40E00064, 10, 0x00000003, 'x', "GPIO 69 alternate function select" },
{ "GAFR2L_70", 0x40E00064, 12, 0x00000003, 'x', "GPIO 70 alternate function select" },
{ "GAFR2L_71", 0x40E00064, 14, 0x00000003, 'x', "GPIO 71 alternate function select" },
{ "GAFR2L_72", 0x40E00064, 16, 0x00000003, 'x', "GPIO 72 alternate function select" },
{ "GAFR2L_73", 0x40E00064, 18, 0x00000003, 'x', "GPIO 73 alternate function select" },
{ "GAFR2L_74", 0x40E00064, 20, 0x00000003, 'x', "GPIO 74 alternate function select" },
{ "GAFR2L_75", 0x40E00064, 22, 0x00000003, 'x', "GPIO 75 alternate function select" },
{ "GAFR2L_76", 0x40E00064, 24, 0x00000003, 'x', "GPIO 76 alternate function select" },
{ "GAFR2L_77", 0x40E00064, 26, 0x00000003, 'x', "GPIO 77 alternate function select" },
{ "GAFR2L_78", 0x40E00064, 28, 0x00000003, 'x', "GPIO 78 alternate function select" },
{ "GAFR2L_79", 0x40E00064, 30, 0x00000003, 'x', "GPIO 79 alternate function select" },

{ "GAFR2U",    0x40E00068,  0, 0xffffffff, 'x', "GPIO Alternate Function Register 2 Upper (4-19)" },
{ "GAFR2U_80", 0x40E00068,  0, 0x00000003, 'x', "GPIO 80 alternate function select" },
{ "GAFR2U_81", 0x40E00068,  2, 0x00000003, 'x', "GPIO 81 alternate function select" },
{ "GAFR2U_82", 0x40E00068,  4, 0x00000003, 'x', "GPIO 82 alternate function select" },
{ "GAFR2U_83", 0x40E00068,  6, 0x00000003, 'x', "GPIO 83 alternate function select" },
{ "GAFR2U_84", 0x40E00068,  8, 0x00000003, 'x', "GPIO 84 alternate function select" },

{ "ICMR",      0x40D00004,  0, 0xffffffff, 'x', "Interrupt Controller Mask Register (4-22)" },
{ "ICMR_IM7",  0x40D00004,  7, 0x00000001, 'x', "Pending IRQ 7 (HWUART) unmasked?" },
{ "ICMR_IM8",  0x40D00004,  8, 0x00000001, 'x', "Pending IRQ 8 (GPIO0) unmasked" },
{ "ICMR_IM9",  0x40D00004,  9, 0x00000001, 'x', "Pending IRQ 9 (GPIO1) unmasked" },
{ "ICMR_IM10", 0x40D00004, 10, 0x00000001, 'x', "Pending IRQ 10 (GPIO2_80) unmasked" },
{ "ICMR_IM11", 0x40D00004, 11, 0x00000001, 'x', "Pending IRQ 11 (USB) unmasked" },
{ "ICMR_IM12", 0x40D00004, 12, 0x00000001, 'x', "Pending IRQ 12 (PMU) unmasked" },
{ "ICMR_IM13", 0x40D00004, 13, 0x00000001, 'x', "Pending IRQ 13 (I2S) unmasked" },
{ "ICMR_IM14", 0x40D00004, 14, 0x00000001, 'x', "Pending IRQ 14 (AC97) unmasked" },
{ "ICMR_IM17", 0x40D00004, 17, 0x00000001, 'x', "Pending IRQ 17 (LCD) unmasked" },
{ "ICMR_IM18", 0x40D00004, 18, 0x00000001, 'x', "Pending IRQ 18 (I2C) unmasked" },
{ "ICMR_IM19", 0x40D00004, 19, 0x00000001, 'x', "Pending IRQ 19 (ICP) unmasked" },
{ "ICMR_IM20", 0x40D00004, 20, 0x00000001, 'x', "Pending IRQ 20 (STUART) unmasked" },
{ "ICMR_IM21", 0x40D00004, 21, 0x00000001, 'x', "Pending IRQ 21 (BTUART) unmasked" },
{ "ICMR_IM22", 0x40D00004, 22, 0x00000001, 'x', "Pending IRQ 22 (FFUART) unmasked" },
{ "ICMR_IM23", 0x40D00004, 23, 0x00000001, 'x', "Pending IRQ 23 (MMC) unmasked" },
{ "ICMR_IM24", 0x40D00004, 24, 0x00000001, 'x', "Pending IRQ 24 (SSP) unmasked" },
{ "ICMR_IM25", 0x40D00004, 25, 0x00000001, 'x', "Pending IRQ 25 (DMA) unmasked" },
{ "ICMR_IM26", 0x40D00004, 26, 0x00000001, 'x', "Pending IRQ 26 (OSMR0) unmasked" },
{ "ICMR_IM27", 0x40D00004, 27, 0x00000001, 'x', "Pending IRQ 27 (OSMR1) unmasked" },
{ "ICMR_IM28", 0x40D00004, 28, 0x00000001, 'x', "Pending IRQ 28 (OSMR2) unmasked" },
{ "ICMR_IM29", 0x40D00004, 29, 0x00000001, 'x', "Pending IRQ 29 (OSMR3) unmasked" },
{ "ICMR_IM30", 0x40D00004, 30, 0x00000001, 'x', "Pending IRQ 30 (RTCCLK) unmasked" },
{ "ICMR_IM31", 0x40D00004, 31, 0x00000001, 'x', "Pending IRQ 31 (RTCALM) unmasked" },

{ "ICLR",      0x40D00008,  0, 0xffffffff, 'x', "Interrupt Controller Level Register (4-23)" },
{ "ICLR_IL7",  0x40D00008,  7, 0x00000001, 'x', "IRQ 8 (HWUART) generates FIQ?" },
{ "ICLR_IL8",  0x40D00008,  8, 0x00000001, 'x', "IRQ 8 (GPIO0) generates FIQ" },
{ "ICLR_IL9",  0x40D00008,  9, 0x00000001, 'x', "IRQ 9 (GPIO1) generates FIQ" },
{ "ICLR_IL10", 0x40D00008, 10, 0x00000001, 'x', "IRQ 10 (GPIO2_80) generates FIQ" },
{ "ICLR_IL11", 0x40D00008, 11, 0x00000001, 'x', "IRQ 11 (USB) generates FIQ" },
{ "ICLR_IL12", 0x40D00008, 12, 0x00000001, 'x', "IRQ 12 (PMU) generates FIQ" },
{ "ICLR_IL13", 0x40D00008, 13, 0x00000001, 'x', "IRQ 13 (I2S) generates FIQ" },
{ "ICLR_IL14", 0x40D00008, 14, 0x00000001, 'x', "IRQ 14 (AC97) generates FIQ" },
{ "ICLR_IL17", 0x40D00008, 17, 0x00000001, 'x', "IRQ 17 (LCD) generates FIQ" },
{ "ICLR_IL18", 0x40D00008, 18, 0x00000001, 'x', "IRQ 18 (I2C) generates FIQ" },
{ "ICLR_IL19", 0x40D00008, 19, 0x00000001, 'x', "IRQ 19 (ICP) generates FIQ" },
{ "ICLR_IL20", 0x40D00008, 20, 0x00000001, 'x', "IRQ 10 (STUART) generates FIQ" },
{ "ICLR_IL21", 0x40D00008, 21, 0x00000001, 'x', "IRQ 21 (BTUART) generates FIQ" },
{ "ICLR_IL22", 0x40D00008, 22, 0x00000001, 'x', "IRQ 22 (FFUART) generates FIQ" },
{ "ICLR_IL23", 0x40D00008, 23, 0x00000001, 'x', "IRQ 23 (MMC) generates FIQ" },
{ "ICLR_IL24", 0x40D00008, 24, 0x00000001, 'x', "IRQ 24 (SSP) generates FIQ" },
{ "ICLR_IL25", 0x40D00008, 25, 0x00000001, 'x', "IRQ 25 (DMA) generates FIQ" },
{ "ICLR_IL26", 0x40D00008, 26, 0x00000001, 'x', "IRQ 26 (OSMR0) generates FIQ" },
{ "ICLR_IL27", 0x40D00008, 27, 0x00000001, 'x', "IRQ 27 (OSMR1) generates FIQ" },
{ "ICLR_IL28", 0x40D00008, 28, 0x00000001, 'x', "IRQ 28 (OSMR2) generates FIQ" },
{ "ICLR_IL29", 0x40D00008, 29, 0x00000001, 'x', "IRQ 29 (OSMR3) generates FIQ" },
{ "ICLR_IL30", 0x40D00008, 30, 0x00000001, 'x', "IRQ 30 (RTCCLK) generates FIQ" },
{ "ICLR_IL31", 0x40D00008, 31, 0x00000001, 'x', "IRQ 31 (RTCALM) generates FIQ" },

{ "ICCR",      0x40D00014,  0, 0xffffffff, 'x', "Interrupt Controller Control Register (4-23)" },
{ "ICCR_DIM",  0x40D00014,  8, 0x00000001, 'x', "ONLY enabled and unmasked IRQ bring CPU from idle to run" },

{ "ICIP",      0x40D00000,  0, 0xffffffff, 'x', "Interrupt Controller IRQ Pending Register (4-24)" },

{ "ICFP",      0x40D0000C,  0, 0xffffffff, 'x', "Interrupt Controller FIQ Pending Register (4-24)" },

{ "ICPR",      0x40D00010,  0, 0xffffffff, 'x', "Interrupt Controller Pending Register (4-25)" },
{ "ICPR_IS7",  0x40D00010,  7, 0x00000001, 'x', "IRQ 7 (HWUART) pending" },
{ "ICPR_IS8",  0x40D00010,  8, 0x00000001, 'x', "IRQ 8 (GPIO0) pending" },
{ "ICPR_IS9",  0x40D00010,  9, 0x00000001, 'x', "IRQ 9 (GPIO1) pending" },
{ "ICPR_IS10", 0x40D00010, 10, 0x00000001, 'x', "IRQ 10 (GPIO2_80) pending" },
{ "ICPR_IS11", 0x40D00010, 11, 0x00000001, 'x', "IRQ 11 (USB) pending" },
{ "ICPR_IS12", 0x40D00010, 12, 0x00000001, 'x', "IRQ 12 (PMU) pending" },
{ "ICPR_IS13", 0x40D00010, 13, 0x00000001, 'x', "IRQ 13 (I2S) pending" },
{ "ICPR_IS14", 0x40D00010, 14, 0x00000001, 'x', "IRQ 14 (AC97) pending" },
{ "ICPR_IS17", 0x40D00010, 17, 0x00000001, 'x', "IRQ 17 (LCD) pending" },
{ "ICPR_IS18", 0x40D00010, 18, 0x00000001, 'x', "IRQ 18 (I2C) pending" },
{ "ICPR_IS19", 0x40D00010, 19, 0x00000001, 'x', "IRQ 19 (ICP) pending" },
{ "ICPR_IS20", 0x40D00010, 20, 0x00000001, 'x', "IRQ 10 (STUART) pending" },
{ "ICPR_IS21", 0x40D00010, 21, 0x00000001, 'x', "IRQ 21 (BTUART) pending" },
{ "ICPR_IS22", 0x40D00010, 22, 0x00000001, 'x', "IRQ 22 (FFUART) pending" },
{ "ICPR_IS23", 0x40D00010, 23, 0x00000001, 'x', "IRQ 23 (MMC) pending" },
{ "ICPR_IS24", 0x40D00010, 24, 0x00000001, 'x', "IRQ 24 (SSP) pending" },
{ "ICPR_IS25", 0x40D00010, 25, 0x00000001, 'x', "IRQ 25 (DMA) pending" },
{ "ICPR_IS26", 0x40D00010, 26, 0x00000001, 'x', "IRQ 26 (OSMR0) pending" },
{ "ICPR_IS27", 0x40D00010, 27, 0x00000001, 'x', "IRQ 27 (OSMR1) pending" },
{ "ICPR_IS28", 0x40D00010, 28, 0x00000001, 'x', "IRQ 28 (OSMR2) pending" },
{ "ICPR_IS29", 0x40D00010, 29, 0x00000001, 'x', "IRQ 29 (OSMR3) pending" },
{ "ICPR_IS30", 0x40D00010, 30, 0x00000001, 'x', "IRQ 30 (RTCCLK) pending" },
{ "ICPR_IS31", 0x40D00010, 31, 0x00000001, 'x', "IRQ 31 (RTCALM) pending" },

{ "RTTR",        0x4090000C,  0, 0xffffffff, 'x', "RTC Trim Register (4-30)" },
{ "RTTR_CK_DIV", 0x4090000C,  0, 0x0000ffff, 'x', "RTC Clock Divider Count" },
{ "RTTR_DEL",    0x4090000C, 16, 0x000003ff, 'x', "RTC Trim delete Count" },
{ "RTTR_LCK",    0x4090000C, 31, 0x00000001, 'x', "RTC Locking for RTTR" },

{ "RTAR",        0x40900010,  0, 0xffffffff, 'x', "RTC Alarm Register (4-30)" },
{ "RTAR_RTMV",   0x40900010,  0, 0xffffffff, 'x', "RTC Target Match Value" },

{ "RCNR",        0x40900000,  0, 0xffffffff, 'x', "RTC Counter Register (4-31)" },
{ "RCNR_RCV",    0x40900000,  0, 0xffffffff, 'x', "RTC Count Value" },

{ "RTSR",        0x40900008,  0, 0xffffffff, 'x', "RTC Status Register (4-32)" },
{ "RTSR_AL",     0x40900008,  0, 0x00000001, 'x', "RTC Alarm Interrupt detected" },
{ "RTSR_HZ",     0x40900008,  1, 0x00000001, 'x', "RTC Hz Interrupt detected" },
{ "RTSR_ALE",    0x40900008,  2, 0x00000001, 'x', "RTC Alarm Interrupt Enable" },
{ "RTSR_HZE",    0x40900008,  3, 0x00000001, 'x', "RTC Hz Interrupt Enable" },

{ "OSMR0",       0x40A00000,  0, 0xffffffff, 'x', "OS Timer Match Register 0 (4-36)" },
{ "OSMR1",       0x40A00004,  0, 0xffffffff, 'x', "OS Timer Match Register 1 (4-36)" },
{ "OSMR2",       0x40A00008,  0, 0xffffffff, 'x', "OS Timer Match Register 2 (4-36)" },
{ "OSMR3",       0x40A0000C,  0, 0xffffffff, 'x', "OS Timer Match Register 3 (4-36)" },

{ "OIER",        0x40A0001C,  0, 0xffffffff, 'x', "OS Timer Interrupt Enable Register (4-36)" },
{ "OIER_E0",     0x40A0001C,  0, 0x00000001, 'x', "OS Interrupt for OSMR0 enabled" },
{ "OIER_E1",     0x40A0001C,  1, 0x00000001, 'x', "OS Interrupt for OSMR1 enabled" },
{ "OIER_E2",     0x40A0001C,  2, 0x00000001, 'x', "OS Interrupt for OSMR2 enabled" },
{ "OIER_E3",     0x40A0001C,  3, 0x00000001, 'x', "OS Interrupt for OSMR3 enabled" },

{ "OWER",        0x40A00018,  0, 0xffffffff, 'x', "OS Timer Watchdog Match Enable Register (4-37)" },
{ "OWER_WME",    0x40A00018,  0, 0x00000001, 'x', "OSMR3 match causes a reset" },

{ "OSCR",        0x40A00010,  0, 0xffffffff, 'x', "OS Timer Count Register (4-37)" },
{ "OSCR_OSCV",   0x40A00010,  0, 0xffffffff, 'x', "OS Timer Count Value" },

{ "OSSR",        0x40A00014,  0, 0xffffffff, 'x', "OS Timer Status Register (4-38)" },
{ "OSSR_M0",     0x40A00014,  0, 0x00000001, 'x', "OS OSMR0 matched OSCR0" },
{ "OSSR_M1",     0x40A00014,  1, 0x00000001, 'x', "OS OSMR1 matched OSCR1" },
{ "OSSR_M2",     0x40A00014,  2, 0x00000001, 'x', "OS OSMR2 matched OSCR2" },
{ "OSSR_M3",     0x40A00014,  3, 0x00000001, 'x', "OS OSMR3 matched OSCR3" },

{ "PWMCTL0",             0x40B00000,  0, 0xffffffff, 'x', "PWM Control Register 0 (4-41)" },
{ "PWMCTL0_PRESCALE",    0x40B00000,  0, 0x0000003f, 'd', "PWM0 Prescale Divisor" },
{ "PWMCTL0_SD",          0x40B00000,  5, 0x00000001, 'x', "PWM0 abrupt shutdown" },

{ "PWMDUTY0",            0x40B00004,  0, 0xffffffff, 'x', "PWM Duty Cycle Register 0 (4-42)" },
{ "PWMDUTY0_DCYCLE",     0x40B00004,  0, 0x000003ff, 'd', "PWM0 Duty Cycle" },
{ "PWMDUTY0_FDCYCLE",    0x40B00004, 10, 0x00000001, 'x', "PWM_OUT0 is set high and does not toggle" },

{ "PWMPERVAL0",          0x40B00008,  0, 0xffffffff, 'x', "PWM Period Control Register 0 (4-43)" },
{ "PWMPERVAL0_PV",       0x40B00008,  0, 0x000003ff, 'd', "PWM0 Period Cycle Length" },

{ "PWMCTL1",             0x40C00000,  0, 0xffffffff, 'x', "PWM Control Register 1 (4-41)" },
{ "PWMCTL1_PRESCALE",    0x40C00000,  0, 0x0000003f, 'd', "PWM1 Prescale Divisor" },
{ "PWMCTL1_SD",          0x40C00000,  5, 0x00000001, 'x', "PWM1 abrupt shutdown" },

{ "PWMDUTY1",            0x40C00004,  0, 0xffffffff, 'x', "PWM Duty Cycle Register 1 (4-42)" },
{ "PWMDUTY1_DCYCLE",     0x40C00004,  0, 0x000003ff, 'd', "PWM1 Duty Cycle" },
{ "PWMDUTY1_FDCYCLE",    0x40C00004, 10, 0x00000001, 'x', "PWM_OUT1 is set high and does not toggle" },

{ "PWMPERVAL1",          0x40C00008,  0, 0xffffffff, 'x', "PWM Period Control Register 1 (4-43)" },
{ "PWMPERVAL1_PV",       0x40C00008,  0, 0x000003ff, 'd', "PWM1 Period Cycle Length" },


{ "LCCR0",     0x44000000,  0, 0xffffffff, 'x', "LCD Controller Control Register 0 (7-23)" },
{ "LCCR0_ENB", 0x44000000,  0, 0x00000001, 'd', "LCD controller enable" },
{ "LCCR0_CMS", 0x44000000,  1, 0x00000001, 'd', "LCD monochrome operation enable" },
{ "LCCR0_SDS", 0x44000000,  2, 0x00000001, 'd', "LCD dual panel display enable" },
{ "LCCR0_LDM", 0x44000000,  3, 0x00000001, 'd', "LCD disable done IRQ disable" },
{ "LCCR0_SFM", 0x44000000,  4, 0x00000001, 'd', "LCD start of frame IRQ disable" },
{ "LCCR0_IUM", 0x44000000,  5, 0x00000001, 'd', "LCD fifo underrun error IRQ disable" },
{ "LCCR0_EFM", 0x44000000,  6, 0x00000001, 'd', "LCD end of frame IRQ disable" },
{ "LCCR0_PAS", 0x44000000,  7, 0x00000001, 'd', "LCD active display enable" },
{ "LCCR0_DPD", 0x44000000,  9, 0x00000001, 'd', "LCD send 8 pixel on L_DD[7:0] at each clock" },
{ "LCCR0_DIS", 0x44000000, 10, 0x00000001, 'd', "LCD controller disable" },
{ "LCCR0_QDM", 0x44000000, 11, 0x00000001, 'd', "LCD quick disable IRQ disable" },
{ "LCCR0_PDD", 0x44000000, 12, 0x000000FF, 'd', "LCD palette DMA request delay" },
{ "LCCR0_BM",  0x44000000, 20, 0x00000001, 'd', "LCD branch start IRQ disable" },
{ "LCCR0_OUM", 0x44000000, 21, 0x00000001, 'd', "LCD fifo underrun IRQ disable" },

{ "LCCR1",     0x44000004,  0, 0xffffffff, 'x', "LCD Controller Control Register 1 (7-26)" },
{ "LCCR1_PPL", 0x44000004,  0, 0x000003ff, 'd', "LCD pixels per line (+1)" },
{ "LCCR1_HSW", 0x44000004, 10, 0x0000003f, 'd', "LCD horizontal sync pulse width (+1)" },
{ "LCCR1_ELW", 0x44000004, 16, 0x000000ff, 'd', "LCD end of line pixel clock wait count (+1)" },
{ "LCCR1_BLW", 0x44000004, 24, 0x000000ff, 'd', "LCD beginning of line pixel clock wait count (+1)" },

{ "LCCR2",     0x44000008,  0, 0xffffffff, 'x', "LCD Controller Control Register 2 (7-28)" },
{ "LCCR2_LPP", 0x44000008,  0, 0x000003ff, 'd', "LCD lines per panel (+1)" },
{ "LCCR2_VSW", 0x44000008, 10, 0x0000003f, 'd', "LCD vertical sync pulse width (+1)" },
{ "LCCR2_EFW", 0x44000008, 16, 0x000000ff, 'd', "LCD end of frame line clock wait count (+1)" },
{ "LCCR2_BFW", 0x44000008, 24, 0x000000ff, 'd', "LCD beginning of frame line clock wait count (+1)" },

{ "LCCR3",     0x4400000C,  0, 0xffffffff, 'x', "LCD Controller Control Register 3 (7-31)" },
{ "LCCR3_PCD", 0x4400000C,  0, 0x000000ff, 'd', "LCD pixel clock divisor (+1)" },
{ "LCCR3_ACB", 0x4400000C,  8, 0x000000ff, 'd', "LCD AC bias pin frequency (+1)" },
{ "LCCR3_API", 0x4400000C, 16, 0x0000000f, 'd', "LCD AC bias pin transitions per interrupt" },
{ "LCCR3_VSP", 0x4400000C, 20, 0x00000001, 'd', "LCD L_FCLK vertical sync polarity active low" },
{ "LCCR3_HSP", 0x4400000C, 21, 0x00000001, 'd', "LCD L_LCLK horizontal sync polarity active low" },
{ "LCCR3_PCP", 0x4400000C, 22, 0x00000001, 'd', "LCD data sampled on falling edge of L_PCLK" },
{ "LCCR3_OEP", 0x4400000C, 23, 0x00000001, 'd', "LCD L_BIAS output enable active low" },
{ "LCCR3_BPP", 0x4400000C, 24, 0x00000007, '<', "LCD bits per pixel" },
{ "LCCR3_DPC", 0x4400000C, 27, 0x00000007, 'd', "LCD double pixel clock rate at L_PCLK" },


{ "FBR0",      0x44000020,  0, 0xffffffff, 'x', "FBR0" },
{ "FBR1",      0x44000020,  0, 0xffffffff, 'x', "FBR1" },
{ "LCSR",      0x44000038,  0, 0xffffffff, 'x', "LCD Controller Status Register (7-40)" },
{ "LIIDR",     0x4400003C,  0, 0xffffffff, 'x', "LCD Controller Interrupt ID Register (7-41)" },
// TODO

{ "TRGBBR",    0x44000040,  0, 0xffffffff, 'x', "TMED RBG Seed Register (7-42)" },
{ "TRGBBR_TRS",0x44000040,  0, 0x000000ff, 'x', "Red Seed" },
{ "TRGBBR_TGS",0x44000040,  8, 0x000000ff, 'x', "Green Seed" },
{ "TRGBBR_TBS",0x44000040, 16, 0x000000ff, 'x', "Blue Seed" },

{ "TCR",       0x44000044,  0, 0xffffffff, 'x', "TMED Control Register (7-44)" },
{ "TCR_COAM",  0x44000044,  0, 0x00000001, 'x', "Color Offset Adjuster Matrix" },
{ "TCR_FNAM",  0x44000044,  1, 0x00000001, 'x', "Frame Number Adjuster Matrix" },
{ "TCR_COAE",  0x44000044,  2, 0x00000001, 'x', "Color Offset Adjuster Enable" },
{ "TCR_FNAME", 0x44000044,  3, 0x00000001, 'x', "Frame Number Adjuster Enable" },
{ "TCR_TVBS",  0x44000044,  4, 0x0000000f, 'd', "Vertical Beat Suppression" },
{ "TCR_THBS",  0x44000044,  8, 0x0000000f, 'd', "Horizontal Beat Suppression" },
{ "TCR_TED",   0x44000044, 14, 0x00000001, 'x', "Energy Distribution Matrix Select" },

{ "FDADR0",    0x44000200,  0, 0xffffffff, 'x', "FDADR0" },
{ "FSADR0",    0x44000204,  0, 0xffffffff, 'x', "FSADR0" },
{ "FIDR0",     0x44000208,  0, 0xffffffff, 'x', "FODR0" },
{ "LDCMD0",    0x4400020C,  0, 0xffffffff, 'x', "LDCMD0" },
{ "FDADR1",    0x44000210,  0, 0xffffffff, 'x', "FDADR1" },
{ "FSADR1",    0x44000214,  0, 0xffffffff, 'x', "FSADR1" },
{ "FIDR1",     0x44000218,  0, 0xffffffff, 'x', "FIDR1" },
{ "LDCMD1",    0x4400021C,  0, 0xffffffff, 'x', "LDCMD1" },
// TODO




{ "MDCNFG",         0x48000000, 0, 0xffffffff, 'x', "SDRAM Configuration Register (6-9)" },
{ "MDCNFG_DE0",     0x48000000, 0, 0x00000001, 'd', "SDRAM enable for partition 0" },
{ "MDCNFG_DE1",     0x48000000, 1, 0x00000001, 'd', "SDRAM enable for partition 1" },
{ "MDCNFG_DWID0",   0x48000000, 2, 0x00000001, 'd', "SDRAM data width (0=32, 1=16)" },
{ "MDCNFG_DCAC0",   0x48000000, 3, 0x00000003, 'd', "Column address bits for partition pair 0/1" },
{ "MDCNFG_DRAC0",   0x48000000, 5, 0x00000003, 'd', "Row address bits for partition pair 0/1" },
{ "MDCNFG_DNB0",    0x48000000, 7, 0x00000001, 'd', "Banks in partition pair 0/1 (0=2, 1=4)" },
{ "MDCNFG_DTC0",    0x48000000, 8, 0x00000003, 'd', "Timing Category for partition pair 0/1" },
{ "MDCNFG_DADDR0",  0x48000000,10, 0x00000001, 'd', "Use alternate addressing for partition pair 0/1" },
{ "MDCNFG_DLATCH0", 0x48000000,11, 0x00000001, 'd', "Return data latching scheme for partition pair 0/1" },
{ "MDCNFG_DSA11110",0x48000000,12, 0x00000001, 'd', "use SA1111 address muxing for partition pair 0/1" },
{ "MDCNFG_DE2",     0x48000000,16, 0x00000001, 'd', "SDRAM enable for partition 2" },
{ "MDCNFG_DE3",     0x48000000,17, 0x00000001, 'd', "SDRAM enable for partition 3" },
{ "MDCNFG_DWID2",   0x48000000,18, 0x00000001, 'd', "SDRAM data width (0=32, 1=16)" },
{ "MDCNFG_DCAC2",   0x48000000,19, 0x00000003, 'd', "Column address bits for partition pair 2/3" },
{ "MDCNFG_DRAC2",   0x48000000,21, 0x00000003, 'd', "Row address bits for partition pair 2/3" },
{ "MDCNFG_DNB2",    0x48000000,23, 0x00000001, 'd', "Banks in partition pair 2/3 (0=2, 1=4)" },
{ "MDCNFG_DTC2",    0x48000000,24, 0x00000003, 'd', "Timing Category for partition pair 2/3" },
{ "MDCNFG_DADDR2",  0x48000000,26, 0x00000001, 'd', "Use alternate addressing for partition pair 2/3" },
{ "MDCNFG_DLATCH2", 0x48000000,27, 0x00000001, 'd', "Return data latching scheme for partition pair 2/3" },
{ "MDCNFG_DSA11112",0x48000000,28, 0x00000001, 'd', "use SA1111 address muxing for partition pair 2/3" },

{ "MDREFR",         0x48000004, 0, 0xffffffff, 'x', "SDRAM Refresh Configuration Register (6-15)" },
{ "MDREFR_DRI",     0x48000004, 0, 0x00000fff, 'x', "SDRAM Refresh intervall, all paritions" },
{ "MDREFR_E0PIN",   0x48000004,12, 0x00000001, 'x', "SDRAM Clock Enable Pin 0 Level" },
{ "MDREFR_K0RUN",   0x48000004,13, 0x00000001, 'x', "SDRAM Clock Run Pin 0" },
{ "MDREFR_K0DB2",   0x48000004,14, 0x00000001, 'x', "SDRAM Clock Pin 0 Divide/2" },
{ "MDREFR_E1PIN",   0x48000004,15, 0x00000001, 'x', "SDRAM Clock Enable Pin 1 Level" },
{ "MDREFR_K1RUN",   0x48000004,16, 0x00000001, 'x', "SDRAM Clock Run Pin 1" },
{ "MDREFR_K1DB2",   0x48000004,17, 0x00000001, 'x', "SDRAM Clock Pin 1 Divide/2" },
{ "MDREFR_K2RUN",   0x48000004,18, 0x00000001, 'x', "SDRAM Clock Run Pin 2" },
{ "MDREFR_K2DB2",   0x48000004,19, 0x00000001, 'x', "SDRAM Clock Pin 2 Divide/2" },
{ "MDREFR_APD",     0x48000004,20, 0x00000001, 'x', "SDRAM Auto Power Down enable" },
{ "MDREFR_SLFRSH",  0x48000004,22, 0x00000001, 'x', "SDRAM Self-Refresh" },
{ "MDREFR_K0FREE",  0x48000004,23, 0x00000001, 'x', "SDRAM Free Running Control for SDCLK0" },
{ "MDREFR_K1FREE",  0x48000004,24, 0x00000001, 'x', "SDRAM Free Running Control for SDCLK1" },
{ "MDREFR_K2FREE",  0x48000004,25, 0x00000001, 'x', "SDRAM Free Running Control for SDCLK2" },

{ "MSC0",           0x48000008, 0, 0xffffffff, 'x', "Asynchronous Static Memory Control Register 0 (6-45)" },
{ "MSC0_RT0",       0x48000008, 0, 0x00000007, 'd', "nCS[0] ROM Type" },
{ "MSC0_RBW0",      0x48000008, 3, 0x00000001, 'd', "nCS[0] ROM Bus Width (1=16bit)" },
{ "MSC0_RDF0",      0x48000008, 4, 0x0000000f, 'd', "nCS[0] ROM Delay First Access" },
{ "MSC0_RDN0",      0x48000008, 8, 0x0000000f, 'd', "nCS[0] ROM Delay Next Access" },
{ "MSC0_RRR0",      0x48000008,12, 0x00000007, 'd', "nCS[0] ROM/SRAM Recovery Time" },
{ "MSC0_RBUFF0",    0x48000008,15, 0x00000001, 'd', "nCS[0] Return Buffer Behavior (1=streaming)" },
{ "MSC0_RT1",       0x48000008,16, 0x00000007, 'd', "nCS[1] ROM Type" },
{ "MSC0_RBW1",      0x48000008,19, 0x00000001, 'd', "nCS[1] ROM Bus Width (1=16bit)" },
{ "MSC0_RDF1",      0x48000008,20, 0x0000000f, 'd', "nCS[1] ROM Delay First Access" },
{ "MSC0_RDN1",      0x48000008,24, 0x0000000f, 'd', "nCS[1] ROM Delay Next Access" },
{ "MSC0_RRR1",      0x48000008,28, 0x00000007, 'd', "nCS[1] ROM/SRAM Recovery Time" },
{ "MSC0_RBUFF1",    0x48000008,31, 0x00000001, 'd', "nCS[1] Return Buffer Behavior (1=streaming)" },

{ "MSC1",           0x4800000C, 0, 0xffffffff, 'x', "Asynchronous Static Memory Control Register 1 (6-45)" },
{ "MSC1_RT2",       0x4800000C, 0, 0x00000007, 'd', "nCS[2] ROM Type" },
{ "MSC1_RBW2",      0x4800000C, 3, 0x00000001, 'd', "nCS[2] ROM Bus Width (1=16bit)" },
{ "MSC1_RDF2",      0x4800000C, 4, 0x0000000f, 'd', "nCS[2] ROM Delay First Access" },
{ "MSC1_RDN2",      0x4800000C, 8, 0x0000000f, 'd', "nCS[2] ROM Delay Next Access" },
{ "MSC1_RRR2",      0x4800000C,12, 0x00000007, 'd', "nCS[2] ROM/SRAM Recovery Time" },
{ "MSC1_RBUFF2",    0x4800000C,15, 0x00000001, 'd', "nCS[2] Return Buffer Behavior (1=streaming)" },
{ "MSC1_RT3",       0x4800000C,16, 0x00000007, 'd', "nCS[3] ROM Type" },
{ "MSC1_RBW3",      0x4800000C,19, 0x00000001, 'd', "nCS[3] ROM Bus Width (1=16bit)" },
{ "MSC1_RDF3",      0x4800000C,20, 0x0000000f, 'd', "nCS[3] ROM Delay First Access" },
{ "MSC1_RDN3",      0x4800000C,24, 0x0000000f, 'd', "nCS[3] ROM Delay Next Access" },
{ "MSC1_RRR3",      0x4800000C,28, 0x00000007, 'd', "nCS[3] ROM/SRAM Recovery Time" },
{ "MSC1_RBUFF3",    0x4800000C,31, 0x00000001, 'd', "nCS[3] Return Buffer Behavior (1=streaming)" },

{ "MSC2",           0x48000010, 0, 0xffffffff, 'x', "Asynchronous Static Memory Control Register 2 (6-45)" },
{ "MSC2_RT4",       0x48000010, 0, 0x00000007, 'd', "nCS[4] ROM Type" },
{ "MSC2_RBW4",      0x48000010, 3, 0x00000001, 'd', "nCS[4] ROM Bus Width (1=16bit)" },
{ "MSC2_RDF4",      0x48000010, 4, 0x0000000f, 'd', "nCS[4] ROM Delay First Access" },
{ "MSC2_RDN4",      0x48000010, 8, 0x0000000f, 'd', "nCS[4] ROM Delay Next Access" },
{ "MSC2_RRR4",      0x48000010,12, 0x00000007, 'd', "nCS[4] ROM/SRAM Recovery Time" },
{ "MSC2_RBUFF4",    0x48000010,15, 0x00000001, 'd', "nCS[4] Return Buffer Behavior (1=streaming)" },
{ "MSC2_RT5",       0x48000010,16, 0x00000007, 'd', "nCS[5] ROM Type" },
{ "MSC2_RBW5",      0x48000010,19, 0x00000001, 'd', "nCS[5] ROM Bus Width (1=16bit)" },
{ "MSC2_RDF5",      0x48000010,20, 0x0000000f, 'd', "nCS[5] ROM Delay First Access" },
{ "MSC2_RDN5",      0x48000010,24, 0x0000000f, 'd', "nCS[5] ROM Delay Next Access" },
{ "MSC2_RRR5",      0x48000010,28, 0x00000007, 'd', "nCS[5] ROM/SRAM Recovery Time" },
{ "MSC2_RBUFF5",    0x48000010,31, 0x00000001, 'd', "nCS[5] Return Buffer Behavior (1=streaming)" },

{ "MECR",           0x48000014, 0, 0xffffffff, 'x', "Expansion Memory Configuration Register (6-61)" },
{ "MECR_NOS",       0x48000014, 0, 0x00000001, 'x', "Number of Sockets (1=2 Sockets)" },
{ "MECR_CIT",       0x48000014, 1, 0x00000001, 'x', "Card inserted" },

{ "SXCNFG",         0x4800001C, 0, 0xffffffff, 'x', "Synchronous Static Memory Configuration Register (6-33)" },
{ "SXCNFG_SXEN0",   0x4800001C, 0, 0x00000001, 'x', "Partition 0 enabled as SX memory" },
{ "SXCNFG_SXEN1",   0x4800001C, 1, 0x00000001, 'x', "Partition 1 enabled as SX memory" },
{ "SXCNFG_SXCL0",   0x4800001C, 2, 0x00000007, 'x', "Partition 0/1 CAS Latency" },
{ "SXCNFG_SXRL0",   0x4800001C, 5, 0x00000007, 'x', "Partition 0/1 RAS Latency" },
{ "SXCNFG_SXRA0",   0x4800001C, 8, 0x00000003, 'x', "Partition 0/1 row address bit count" },
{ "SXCNFG_SXCA0",   0x4800001C,10, 0x00000003, 'x', "Partition 0/1 column address bit count" },
{ "SXCNFG_SXTP0",   0x4800001C,12, 0x00000003, 'x', "Partition 0/1 memory type" },
{ "SXCNFG_SXLATCH0",0x4800001C,14, 0x00000001, 'x', "Partition 0/1 return data with return clock" },
{ "SXCNFG_SXEN2",   0x4800001C,16, 0x00000001, 'x', "Partition 2 enabled as SX memory" },
{ "SXCNFG_SXEN3",   0x4800001C,17, 0x00000001, 'x', "Partition 3 enabled as SX memory" },
{ "SXCNFG_SXCL2",   0x4800001C,18, 0x00000007, 'x', "Partition 2/3 CAS Latency" },
{ "SXCNFG_SXRL2",   0x4800001C,21, 0x00000007, 'x', "Partition 2/3 RAS Latency" },
{ "SXCNFG_SXRA2",   0x4800001C,24, 0x00000003, 'x', "Partition 2/3 row address bit count" },
{ "SXCNFG_SXCA2",   0x4800001C,26, 0x00000003, 'x', "Partition 2/3 column address bit count" },
{ "SXCNFG_SXTP2",   0x4800001C,28, 0x00000003, 'x', "Partition 2/3 memory type" },
{ "SXCNFG_SXLATCH2",0x4800001C,30, 0x00000001, 'x', "Partition 2/3 return data with return clock" },

{ "SXMRS",          0x48000024, 0, 0xffffffff, 'x', "MRS value to be written to SX Memory (6-38)" },

{ "MCMEM0",         0x48000028, 0, 0xffffffff, 'x', "MEM Control for PCMCIA Socket 0 (6-58)" },
{ "MCMEM0_SET",     0x48000028, 0, 0x0000007f, 'd', "Address set time" },
{ "MCMEM0_ASST",    0x48000028, 7, 0x0000001f, 'd', "Command assertion time" },
{ "MCMEM0_HOLD",    0x48000028,14, 0x0000003f, 'd', "Address hold time" },

{ "MCMEM1",         0x4800002C, 0, 0xffffffff, 'x', "MEM Control for PCMCIA Socket 1 (6-58)" },
{ "MCMEM1_SET",     0x4800002C, 0, 0x0000007f, 'd', "Address set time" },
{ "MCMEM1_ASST",    0x4800002C, 7, 0x0000001f, 'd', "Command assertion time" },
{ "MCMEM1_HOLD",    0x4800002C,14, 0x0000003f, 'd', "Address hold time" },

{ "MCATT0",         0x48000030, 0, 0xffffffff, 'x', "ATT Control for PCMCIA Socket 0 (6-59)" },
{ "MCATT0_SET",     0x48000030, 0, 0x0000007f, 'd', "Address set time" },
{ "MCATT0_ASST",    0x48000030, 7, 0x0000001f, 'd', "Command assertion time" },
{ "MCATT0_HOLD",    0x48000030,14, 0x0000003f, 'd', "Address hold time" },

{ "MCATT1",         0x48000034, 0, 0xffffffff, 'x', "ATT Control for PCMCIA Socket 1 (6-59)" },
{ "MCATT1_SET",     0x48000034, 0, 0x0000007f, 'd', "Address set time" },
{ "MCATT1_ASST",    0x48000034, 7, 0x0000001f, 'd', "Command assertion time" },
{ "MCATT1_HOLD",    0x48000034,14, 0x0000003f, 'd', "Address hold time" },

{ "MCIO0",          0x48000038, 0, 0xffffffff, 'x', "I/O Control for PCMCIA Socket 0 (6-59)" },
{ "MCIO0_SET",      0x48000038, 0, 0x0000007f, 'd', "Address set time" },
{ "MCIO0_ASST",     0x48000038, 7, 0x0000001f, 'd', "Command assertion time" },
{ "MCIO0_HOLD",     0x48000038,14, 0x0000003f, 'd', "Address hold time" },

{ "MCIO1",          0x4800003C, 0, 0xffffffff, 'x', "I/O Control for PCMCIA Socket 1 (6-59)" },
{ "MCIO1_SET",      0x4800003C, 0, 0x0000007f, 'd', "Address set time" },
{ "MCIO1_ASST",     0x4800003C, 7, 0x0000001f, 'd', "Command assertion time" },
{ "MCIO1_HOLD",     0x4800003C,14, 0x0000003f, 'd', "Address hold time" },

{ "MDMRS",          0x48000040, 0, 0xffffffff, 'x', "SDRAM Mode Register Set Configuration Register (6-12)" },
{ "MDMRS_MDBL0",    0x48000040, 0, 0x00000007, 'x', "SDRAM Partition 0/1 burst length" },
{ "MDMRS_MDADD0",   0x48000040, 3, 0x00000001, 'x', "SDRAM Partition 0/1 burst type" },
{ "MDMRS_MDCL0",    0x48000040, 4, 0x00000007, 'x', "SDRAM Partition 0/1 CAS latency" },
{ "MDMRS_MDMRS0",   0x48000040, 7, 0x000000ff, 'x', "MRS value to be written to SDRAM Partition 0/1" },
{ "MDMRS_MDBL2",    0x48000040,16, 0x00000007, 'x', "SDRAM Partition 2/3 burst length" },
{ "MDMRS_MDADD2",   0x48000040,19, 0x00000001, 'x', "SDRAM Partition 2/3 burst type" },
{ "MDMRS_MDCL2",    0x48000040,20, 0x00000007, 'x', "SDRAM Partition 2/3 CAS latency" },
{ "MDMRS_MDMRS2",   0x48000040,23, 0x000000ff, 'x', "MRS value to be written to SDRAM Partition 2/3" },

{ "BOOTDEF",        0x48000044, 0, 0xffffffff, 'x', "Boot Time Defaults (6-73)" },
{ "BOOTDEF_BOOTSEL",0x48000044, 0, 0x00000007, 'x', "Boot Configuration at BOOT_SEL pins" },
{ "BOOTDEF_PKGTYPE",0x48000044, 3, 0x00000001, 'x', "Processor type, 1 for PXA250" },

{ "MDMRSLP",        0x48000058, 0, 0xffffffff, 'x', "Low-Power SDRAM Mode Register Set Configuration Register (6-14)" },
// TODO



{ "MMC_STRPCL",			0x41100000, 0, 0xffffffff, 'x', "MMC Start/Stop Clock (15-23)" },

{ "MMC_STAT",			0x41100004, 0, 0xffffffff, 'x', "MMC Status Register (15-24)" },
{ "MMC_STAT_READ_TIME_OUT",	0x41100004, 0, 0x00000001, 'x', "Read Time Out" },
{ "MMC_STAT_TIME_OUT_RESP",	0x41100004, 1, 0x00000001, 'x', "Time Out Response" },
{ "MMC_STAT_CRC_WRITE_ERROR",	0x41100004, 2, 0x00000001, 'x', "CRC Write Error" },
{ "MMC_STAT_CRC_READ_ERR",	0x41100004, 3, 0x00000001, 'x', "CRC Read Error" },
{ "MMC_STAT_SPI_READ_ERR_TKN",	0x41100004, 4, 0x00000001, 'x', "SPI Read Error Token" },
{ "MMC_STAT_RES_CRC_ERR",	0x41100004, 5, 0x00000001, 'x', "Response CRC Error" },
{ "MMC_STAT_XMIT_FIFO_EMPTY",	0x41100004, 6, 0x00000001, 'x', "Transmit FIFO Empty" },
{ "MMC_STAT_RECV_FIFO_EMPTY",	0x41100004, 7, 0x00000001, 'x', "Receive FIFO Empty" },
{ "MMC_STAT_CLK_EN",		0x41100004, 8, 0x00000001, 'x', "Clock Enabled" },
{ "MMC_STAT_DATA_TRAN_DONE",	0x41100004,11, 0x00000001, 'x', "Data Transmission Done" },
{ "MMC_STAT_PRG_DONE",		0x41100004,12, 0x00000001, 'x', "Program Done" },
{ "MMC_STAT_END_CMD_RES",	0x41100004,13, 0x00000001, 'x', "End Command Response" },

{ "MMC_CLKRT",			0x41100008, 0, 0xffffffff, 'x', "MMC Clock Read Timeout Register (15-26)" },
{ "MMC_CLK_RATE",		0x41100008, 0, 0x00000007, 'x', "Read Time Out bitmask" },

{ "MMC_SPI",			0x4110000c, 0, 0xffffffff, 'x', "MMC SPI mode (15-27)" },
{ "MMC_SPI_EN",			0x4110000c, 0, 0x00000001, 'x', "SPI mode enabled" },
{ "MMC_SPI_CRC_ON",		0x4110000c, 1, 0x00000001, 'x', "CRC generation enabled" },
{ "MMC_SPI_CS_EN",		0x4110000c, 2, 0x00000001, 'x', "SPI chip select enabled" },
{ "MMC_SPI_CS_ADDRESS",		0x4110000c, 3, 0x00000001, 'x', "CS0 enabled" },

{ "MMC_CMDAT",			0x41100010, 0, 0xffffffff, 'x', "MMC Command Data (15-28)" },
{ "MMC_CMDAT_RF",		0x41100010, 0, 0x00000003, 'x', "response format" },
{ "MMC_CMDAT_DATA_EN",		0x41100010, 2, 0x00000001, 'x', "current cmd includes data transfer" },
{ "MMC_CMDAT_WRITE",		0x41100010, 3, 0x00000001, 'x', "data transfer is a write" },
{ "MMC_CMDAT_STREAM",		0x41100010, 4, 0x00000001, 'x', "data transfer is in stream mode" },
{ "MMC_CMDAT_BUSY",		0x41100010, 5, 0x00000001, 'x', "busy signal is expected after data transfer" },
{ "MMC_CMDAT_INIT",		0x41100010, 6, 0x00000001, 'x', "precede cmd with 80 clocks" },
{ "MMC_CMDAT_DMA_EN",		0x41100010, 7, 0x00000001, 'x', "enable DMA mode" },

{ "MMC_RESTO",			0x41100014, 0, 0xffffffff, 'x', "MMC Response Time Out (15-29)" },
{ "MMC_RESTO_TO",		0x41100014, 0, 0x0000007f, 'd', "clocks before a response time out" },

{ "MMC_RDTO",			0x41100018, 0, 0xffffffff, 'x', "MMC Read Time Out (15-29)" },
{ "MMC_RDTO_TO",		0x41100018, 0, 0x0000ffff, 'd', "time until read time out" },

{ "MMC_BLKLEN",			0x4110001C, 0, 0xffffffff, 'x', "MMC Block Len Register (15-30)" },
{ "MMC_BLKLEN_LEN",		0x4110001C, 0, 0x000003ff, 'd', "Number of bytes in the block" },

{ "MMC_NOB",			0x41100020, 0, 0xffffffff, 'x', "MMC Block Number Register (15-30)" },
{ "MMC_NOB_N",			0x41100020, 0, 0x0000ffff, 'd', "number of blocks" },

{ "MMC_PRTBUF",			0x41100024, 0, 0xffffffff, 'x', "MMC Partial Buffer Register (15-31)" },
{ "MMC_PRTBUF_FULL",		0x41100024, 0, 0x00000001, 'x', "Buffer is partially full" },

{ "MMC_IMASK",			0x41100028, 0, 0xffffffff, 'x', "MMC Interrupt Mask Register (15-31)" },
{ "MMC_IMASK_DATATRAN",		0x41100028, 0, 0x00000001, 'x', "Data Transfer Done masked" },
{ "MMC_IMASK_PRGDONE",		0x41100028, 1, 0x00000001, 'x', "Programming Done masked" },
{ "MMC_IMASK_ENDCMD",		0x41100028, 2, 0x00000001, 'x', "End Command Response masked" },
{ "MMC_IMASK_STOPCMD",		0x41100028, 3, 0x00000001, 'x', "Ready for Stop Transaction Command masked" },
{ "MMC_IMASK_CLOCKOFF",		0x41100028, 4, 0x00000001, 'x', "Clock Is Off masked" },
{ "MMC_IMASK_RXFIFO",		0x41100028, 5, 0x00000001, 'x', "Receive FIFO Read Request masked" },
{ "MMC_IMASK_TXFIFO",		0x41100028, 6, 0x00000001, 'x', "Transmit FIFO Write Request masked" },

{ "MMC_IREG",			0x4110002c, 0, 0xffffffff, 'x', "MMC Interrupt Register (15-33)" },
{ "MMC_IREG_DATATRAN",		0x4110002c, 0, 0x00000001, 'x', "Data Transfer Done or Read TimeOut occured" },
{ "MMC_IREG_PRGDONE",		0x4110002c, 1, 0x00000001, 'x', "Card has finished programming" },
{ "MMC_IREG_ENDCMD",		0x4110002c, 2, 0x00000001, 'x', "MMC has received response or Response TimeOut" },
{ "MMC_IREG_STOPCMD",		0x4110002c, 3, 0x00000001, 'x', "MMC is ready for the Stop Transaction Command" },
{ "MMC_IREG_CLOCKOFF",		0x4110002c, 4, 0x00000001, 'x', "MMC clock has been turned off" },
{ "MMC_IREG_RXFIFO",		0x4110002c, 5, 0x00000001, 'x', "Request for data read from receive FIFO" },
{ "MMC_IREG_TXFIFO",		0x4110002c, 6, 0x00000001, 'x', "Request to data write to transmit FIFO" },

{ "MMC_CMD",			0x41100030, 0, 0xffffffff, 'x', "MMC Command Register (15-34)" },
{ "MMC_CMD_INDEX",		0x41100030, 0, 0x0000003f, 'x', "command index" },

{ "MMC_ARGH",			0x41100034, 0, 0xffffffff, 'x', "MMC Higher Argument Register (15-36)" },
{ "MMC_ARGH_ARG",		0x41100034, 0, 0x0000ffff, 'x', "upper 16 bits of command argument" },

{ "MMC_ARGL",			0x41100038, 0, 0xffffffff, 'x', "MMC Lower Argument Register (15-36)" },
{ "MMC_ARGL_ARG",		0x41100038, 0, 0x0000ffff, 'x', "upper 16 bits of command argument" },


#if defined(CONFIG_ARCH_PXA_IDP) || defined(CONFIG_ARCH_RAMSES)
// CS5+0x03C00000  CPLD		0x14000000
// CS1             Alt-Flash	0x04000000
// CS0             Flash	0x00000000

{ "CPLD_PWR",			0x17C00004, 0, 0x000000ff, 'x', "CPLD_PERIPH_PWR" },
{ "CPLD_PWR_CORE",		0x17C00004, 0, 0x00000001, 'd', "Variable core enable - latch value in first" },
#ifdef CONFIG_ARCH_PXA_IDP
{ "CPLD_PWR_MQ",		0x17C00004, 2, 0x00000001, 'd', "MQ1132 power switch" },
#else
{ "CPLD_PWR_SL811HS",		0x17C00004, 2, 0x00000001, 'd', "SL811HS power switch" },
#endif
{ "CPLD_PWR_PER",		0x17C00004, 3, 0x00000001, 'd', "peripheral power enable" },
{ "CPLD_PWR_RST",		0x17C00004, 4, 0x00000001, 'd', "peripheral reset" },

{ "CPLD_LED",			0x17C00008, 0, 0x000000ff, 'x', "CPLD_LED_CONTROL" },
{ "CPLD_LED_CIR",		0x17C00008, 0, 0x00000001, 'd', "CIR" },
{ "CPLD_LED_HB",		0x17C00008, 5, 0x00000001, 'd', "red LED (0=on)" },
{ "CPLD_LED_BUSY",		0x17C00008, 6, 0x00000001, 'd', "green LED (0=on)" },
{ "CPLD_LED_FLASH",		0x17C00008, 7, 0x00000001, 'd', "red LED flash enable" },

{ "CPLD_KBD_COL_HIGH",		0x17C0000C, 0, 0x000000ff, 'x', "CPLD" },

{ "CPLD_KBD_COL_LOW",		0x17C00010, 0, 0x000000ff, 'x', "CPLD" },

{ "CPLD_PCCARD_EN",		0x17C00014, 0, 0x000000ff, 'x', "CPLD PC-Card Enable" },
{ "CPLD_PCC0_ENABLE",		0x17C00014, 0, 0x00000001, 'd', "PC-Card 0 enable" },
{ "CPLD_PCC1_ENABLE",		0x17C00014, 1, 0x00000001, 'd', "PC-Card 1 enable" },
{ "CPLD_PCC0_RESET",		0x17C00014, 6, 0x00000001, 'd', "PC-Card 0 reset" },
{ "CPLD_PCC1_RESET",		0x17C00014, 7, 0x00000001, 'd', "PC-Card 1 reset" },

/*
{ "CPLD_GPIOH_DIR",		0x17C00018, 0, 0xffffffff, 'x', "CPLD" },
{ "CPLD_GPIOH_VALUE",		0x17C0001C, 0, 0xffffffff, 'x', "CPLD" },
{ "CPLD_GPIOL_DIR",		0x17C00020, 0, 0xffffffff, 'x', "CPLD" },
{ "CPLD_GPIOL_VALUE",		0x17C00024, 0, 0xffffffff, 'x', "CPLD" },

      WHEN "00110" =>
        data(7) <= l3_data_out;         -- L3 IIS control bus - direction of data bit
        data(6) <= '0';
        data(5) <= '0';
        data(4) <= '0';
        data(3) <= '0';
        data(2) <= '0';
        data(1) <= gpslow_out(1);       -- direction of pld_gpio_09
        data(0) <= gpslow_out(0);       -- direction of pld_gpio_08

      WHEN "00111" =>                   -- gpio on async bus
        data(7) <= l3_data_io;          -- L3 IIS control bus - always reads pin
        data(6) <= l3_clk;              -- L3 IIS control bus - read back clock
        data(5) <= l3_mode;             -- L3 IIS control bus - read back mode
        data(4) <= '0';
        data(3) <= '0';
        data(2) <= '0';
        data(1) <= gpslow_io(1);        -- gpio on async bus
        data(0) <= gpslow_io(0);        -- gpio on async bus

      WHEN "01000" =>
        data <= gp_out;                 -- direction of pld_gpio_07 through 00

      WHEN "01001" =>                   -- gpio on high speed bus
        data <= gp_io;
*/

{ "CPLD_PCCARD_PWR",		0x17C00028, 0, 0x000000ff, 'x', "CPLD PC-Card Power" },
{ "CPLD_PCC0_PWR0",		0x17C00028, 0, 0x00000001, 'd', "PC-Card 0 Pwr 0" },
{ "CPLD_PCC0_PWR1",		0x17C00028, 1, 0x00000001, 'd', "PC-Card 0 Pwr 1" },
{ "CPLD_PCC0_PWR2",		0x17C00028, 2, 0x00000001, 'd', "PC-Card 0 Pwr 2" },
{ "CPLD_PCC0_PWR3",		0x17C00028, 3, 0x00000001, 'd', "PC-Card 0 Pwr 3" },
{ "CPLD_PCC1_PWR0",		0x17C00028, 4, 0x00000001, 'd', "PC-Card 1 Pwr 0" },
{ "CPLD_PCC1_PWR1",		0x17C00028, 5, 0x00000001, 'd', "PC-Card 1 Pwr 1" },
{ "CPLD_PCC1_PWR2",		0x17C00028, 6, 0x00000001, 'd', "PC-Card 1 Pwr 2" },
{ "CPLD_PCC1_PWR3",		0x17C00028, 7, 0x00000001, 'd', "PC-Card 1 Pwr 3" },

{ "CPLD_MISC",			0x17C0002C, 0, 0x000000ff, 'x', "CPLD_MISC_CTRL" },
{ "CPLD_MISC_SER1EN",		0x17C0002C, 0, 0x00000001, 'd', "RS-232 on FF UART enable" },
{ "CPLD_MISC_SER2EN",		0x17C0002C, 1, 0x00000001, 'd', "RS-232 on BT UART enable" },
{ "CPLD_MISC_SER3EN",		0x17C0002C, 2, 0x00000001, 'd', "RS-232 on ST UART enable" },
{ "CPLD_MISC_IRDAFIR",		0x17C0002C, 3, 0x00000001, 'd', "IrDA FIR enable" },
{ "CPLD_MISC_IRDAMD0",		0x17C0002C, 4, 0x00000001, 'd', "IrDA mode 0" },
{ "CPLD_MISC_IRDAMD1",		0x17C0002C, 5, 0x00000001, 'd', "IrDA mode 1" },
#ifdef CONFIG_ARCH_PXA_IDP
{ "CPLD_MISC_I2SPWR",		0x17C0002C, 7, 0x00000001, 'd', "UDA1341 power switch" },
#endif

{ "CPLD_LCD",			0x17C00030, 0, 0x000000ff, 'x', "CPLD LCD Control", },
#ifdef CONFIG_ARCH_PXA_IDP
{ "CPLD_LCD_PWR",		0x17C00030, 0, 0x00000001, 'd', "LCD Power" },
{ "CPLD_LCD_BACKLIGHT",		0x17C00030, 1, 0x00000001, 'd', "LCD Backlight" },
{ "CPLD_LCD_VLCD",		0x17C00030, 2, 0x00000001, 'd', "LCD VLCD" },
#else
{ "CPLD_LCD_VCC",		0x17C00030, 0, 0x00000001, 'd', "LCD VCC" },
{ "CPLD_LCD_DISPOFF",		0x17C00030, 2, 0x00000001, 'd', "LCD nDISPOFF" },
#endif

{ "CPLD_FLASH",			0x17C00034, 0, 0x000000ff, 'x', "CPLD Flash Control" },
{ "CPLD_FLASH_WE",		0x17C00034, 0, 0x00000001, 'd', "CPLD StrataFlash Write Enable" },
#ifdef CONFIG_ARCH_PXA_IDP
{ "CPLD_FLASH_MWE",		0x17C00034, 1, 0x00000001, 'd', "CPLD MPlus Write Enable" },
{ "CPLD_FLASH_MOE",		0x17C00034, 2, 0x00000001, 'd', "CPLD MPlus Output Enable" },

{ "CPLD_CS",			0x17C00038, 0, 0x000000ff, 'x', "CPLD Chip Select Register" },
{ "CPLD_CS_CS0EN",		0x17C00038, 0, 0x00000001, 'd', "CPLD Chip Select 0 Enable" },
{ "CPLD_CS_CS1EN",		0x17C00038, 1, 0x00000001, 'd', "CPLD Chip Select 1 Enable" },
{ "CPLD_CS_CS2EN",		0x17C00038, 2, 0x00000001, 'd', "CPLD Chip Select 2 Enable" },
{ "CPLD_CS_CS3EN",		0x17C00038, 3, 0x00000001, 'd', "CPLD Chip Select 3 Enable" },
{ "CPLD_CS_CS4EN",		0x17C00038, 4, 0x00000001, 'd', "CPLD Chip Select 4 Enable" },
//{ "CPLD_CS_CS5EN",		0x17C00038, 4, 0x00000001, 'd', "CPLD Chip Select 5 Enable" },
#endif

{ "CPLD_KB_ROW",		0x17C00050, 0, 0x000000ff, 'x', "CPLD" },

{ "CPLD_PCCARD0_STATUS",	0x17C00054, 0, 0x000000ff, 'x', "CPLD PC-Card 0 Status" },
{ "CPLD_PCC0_VS1",              0x17C00054, 0, 0x00000001, 'd', "PC-Card 0 VS1" },
{ "CPLD_PCC0_VS2",              0x17C00054, 1, 0x00000001, 'd', "PC-Card 0 VS2" },
{ "CPLD_PCC0_BVD1",             0x17C00054, 2, 0x00000001, 'd', "PC-Card 0 BVD1" },
{ "CPLD_PCC0_BVD2",             0x17C00054, 3, 0x00000001, 'd', "PC-Card 0 BVD2" },
{ "CPLD_PCC0_INPACK",           0x17C00054, 4, 0x00000001, 'd', "PC-Card 0 INPACK" },
{ "CPLD_PCC0_IRQ",              0x17C00054, 5, 0x00000001, 'd', "PC-Card 0 IRQ" },
{ "CPLD_PCC0_STRESET",          0x17C00054, 6, 0x00000001, 'd', "PC-Card 0 RESET" },
{ "CPLD_PCC0_WRPROT",           0x17C00054, 7, 0x00000001, 'd', "PC-Card 0 WRPROT" },

{ "CPLD_PCCARD1_STATUS",	0x17C00058, 0, 0x000000ff, 'x', "CPLD PC-Card 1 Status" },
{ "CPLD_PCC1_VS1",              0x17C00058, 0, 0x00000001, 'd', "PC-Card 1 VS1" },
{ "CPLD_PCC1_VS2",              0x17C00058, 1, 0x00000001, 'd', "PC-Card 1 VS2" },
{ "CPLD_PCC1_BVD1",             0x17C00058, 2, 0x00000001, 'd', "PC-Card 1 BVD1" },
{ "CPLD_PCC1_BVD2",             0x17C00058, 3, 0x00000001, 'd', "PC-Card 1 BVD2" },
{ "CPLD_PCC1_INPACK",           0x17C00058, 4, 0x00000001, 'd', "PC-Card 1 INPACK" },
{ "CPLD_PCC1_IRQ",              0x17C00058, 5, 0x00000001, 'd', "PC-Card 1 IRQ" },
{ "CPLD_PCC1_STRESET",          0x17C00058, 6, 0x00000001, 'd', "PC-Card 1 RESET" },
{ "CPLD_PCC1_WRPROT",           0x17C00058, 7, 0x00000001, 'd', "PC-Card 1 WRPROT" },

{ "CPLD_MISC_STATUS",		0x17C0005C, 0, 0x000000ff, 'x', "CPLD Misc Status" },
{ "CPLD_MISC_USB_D_CON",        0x17C0005C, 0, 0x00000001, 'd', "Charge status" },
{ "CPLD_MISC_WALL_IN",          0x17C0005C, 1, 0x00000001, 'd', "Charge status" },
{ "CPLD_MISC_CHG_STS",          0x17C0005C, 2, 0x00000001, 'd', "Charge status" },
{ "CPLD_MISC_MMC_WPROT",        0x17C0005C, 7, 0x00000001, 'd', "MMC write protect" },

{ "CPLD_VER_YEAR",		0x17C00060, 0, 0x000000ff, 'x', "CPLD Year" },
{ "CPLD_VER_MONTH",		0x17C00064, 0, 0x000000ff, 'x', "CPLD Month" },
{ "CPLD_VER_DAY",		0x17C00068, 0, 0x000000ff, 'x', "CPLD Day" },
{ "CPLD_VER_REV",		0x17C0006C, 0, 0x000000ff, 'x', "CPLD Revision" },

{ "CPLD_VSTAT",			0x17C0007C, 0, 0x000000ff, 'x', "CPLD Voltage Status" },
#ifdef CONFIG_ARCH_PXA_IDP
{ "CPLD_BSTAT_V3GOOD",		0x17C0007C, 0, 0x00000001, 'x', "v3good" },
#endif
{ "CPLD_BSTAT_BWE",		0x17C0007C, 1, 0x00000001, 'x', "bwe" },

#endif

#if defined(CONFIG_ARCH_RAMSES)
{ "UARTA_RHR",			0x0C002E00, 0, 0xffffffff, 'x', "UART A RHR/THR" },
{ "UARTA_IER",			0x0C002E04, 0, 0xffffffff, 'x', "UART A IER" },
{ "UARTA_FCR",			0x0C002E08, 0, 0xffffffff, 'x', "UART A FCR/IIR" },
{ "UARTA_LCR",			0x0C002E0C, 0, 0xffffffff, 'x', "UART A LCR" },
{ "UARTA_MCR",			0x0C002E10, 0, 0xffffffff, 'x', "UART A MCR" },
{ "UARTA_LSR",			0x0C002E14, 0, 0xffffffff, 'x', "UART A LSR" },
{ "UARTA_MSR",			0x0C002E18, 0, 0xffffffff, 'x', "UART A MSR" },
{ "UARTA_SPR",			0x0C002E1C, 0, 0xffffffff, 'x', "UART A SPR" },

{ "UARTB_RHR",			0x0C002D00, 0, 0xffffffff, 'x', "UART B RHR/THR" },
{ "UARTB_IER",			0x0C002D04, 0, 0xffffffff, 'x', "UART B IER" },
{ "UARTB_FCR",			0x0C002D08, 0, 0xffffffff, 'x', "UART B FCR/IIR" },
{ "UARTB_LCR",			0x0C002D0C, 0, 0xffffffff, 'x', "UART B LCR" },
{ "UARTB_MCR",			0x0C002D10, 0, 0xffffffff, 'x', "UART B MCR" },
{ "UARTB_LSR",			0x0C002D14, 0, 0xffffffff, 'x', "UART B LSR" },
{ "UARTB_MSR",			0x0C002D18, 0, 0xffffffff, 'x', "UART B MSR" },
{ "UARTB_SPR",			0x0C002D1C, 0, 0xffffffff, 'x', "UART B SPR" },

{ "UARTD_RHR",			0x0C002B00, 0, 0xffffffff, 'x', "UART C RHR/THR" },
{ "UARTD_IER",			0x0C002B04, 0, 0xffffffff, 'x', "UART C IER" },
{ "UARTD_FCR",			0x0C002B08, 0, 0xffffffff, 'x', "UART C FCR/IIR" },
{ "UARTD_LCR",			0x0C002B0C, 0, 0xffffffff, 'x', "UART C LCR" },
{ "UARTD_MCR",			0x0C002B10, 0, 0xffffffff, 'x', "UART C MCR" },
{ "UARTD_LSR",			0x0C002B14, 0, 0xffffffff, 'x', "UART C LSR" },
{ "UARTD_MSR",			0x0C002B18, 0, 0xffffffff, 'x', "UART C MSR" },
{ "UARTD_SPR",			0x0C002B1C, 0, 0xffffffff, 'x', "UART C SPR" },

{ "UARTD_RHR",			0x0C002700, 0, 0xffffffff, 'x', "UART D RHR/THR" },
{ "UARTD_IER",			0x0C002704, 0, 0xffffffff, 'x', "UART D IER" },
{ "UARTD_FCR",			0x0C002708, 0, 0xffffffff, 'x', "UART D FCR/IIR" },
{ "UARTD_LCR",			0x0C00270C, 0, 0xffffffff, 'x', "UART D LCR" },
{ "UARTD_MCR",			0x0C002710, 0, 0xffffffff, 'x', "UART D MCR" },
{ "UARTD_LSR",			0x0C002714, 0, 0xffffffff, 'x', "UART D LSR" },
{ "UARTD_MSR",			0x0C002718, 0, 0xffffffff, 'x', "UART D MSR" },
{ "UARTD_SPR",			0x0C00271C, 0, 0xffffffff, 'x', "UART D SPR" },

#endif

};



#define MAP_SIZE 4096
#define MAP_MASK ( MAP_SIZE - 1 )

static int getmem(u32 addr)
{
   void *map, *regaddr;
   u32 val;

   //printf("getmem(0x%08x)\n", addr);

   if (fd == -1) {
      fd = open("/dev/mem", O_RDWR | O_SYNC);
      if (fd<0) {
          perror("open(\"/dev/mem\")");
          exit(1);
      }
   }

   map = mmap(0,
              MAP_SIZE,
              PROT_READ | PROT_WRITE,
              MAP_SHARED,
              fd,
              addr & ~MAP_MASK
             );
   if (map == (void*)-1 ) {
       perror("mmap()");
       exit(1);
   }

   regaddr = map + (addr & MAP_MASK);

   val = *(u32*) regaddr;
   munmap(0,MAP_SIZE);

   return val;
}

static void putmem(u32 addr, u32 val)
{
   void *map, *regaddr;
   static int fd = -1;

   //printf("putmem(0x%08x, 0x%08x)\n", addr, val);

   if (fd == -1) {
      fd = open("/dev/mem", O_RDWR | O_SYNC);
      if (fd<0) {
          perror("open(\"/dev/mem\")");
          exit(1);
      }
   }

   map = mmap(0,
              MAP_SIZE,
              PROT_READ | PROT_WRITE,
              MAP_SHARED,
              fd,
              addr & ~MAP_MASK
             );
   if (map == (void*)-1 ) {
       perror("mmap()");
       exit(1);
   }

   regaddr = map + (addr & MAP_MASK);

   *(u32*) regaddr = val;
   munmap(0,MAP_SIZE);
}

static u32 lastaddr = 0;
static u32 newaddr = 1;
static u32 data = 0;
static u32 shiftdata;


static void dumpentry(int i)
{
   int j;

   if (regs[i].addr != lastaddr) newaddr = 1;
   if (newaddr) {
       newaddr = 0;
       lastaddr = regs[i].addr;
       data = getmem(lastaddr);
       printf("\n%s\n", regs[i].desc);
       printf("%-24s 0x%08x  ", regs[i].name, data);
       shiftdata = data;
       for (j=32; j>0; j--) {
           printf("%c", shiftdata & 0x80000000 ? '1' : '0');
           shiftdata = shiftdata << 1;
           if (j==9 || j==17 || j==25) printf(" ");
       }

       printf("\n");
   }

   if (regs[i].shift != 0  ||  regs[i].mask != 0xffffffff) {
       shiftdata = (data >> regs[i].shift) & regs[i].mask;
       printf("%-25s  ", regs[i].name);
       switch (regs[i].type) {
          case 'x': printf("%8x", shiftdata);
                    break;
          case '<': printf("%8u", 1 << shiftdata);
                    break;
          default:
                    printf("%8u", shiftdata);
       }
       printf("  %s\n", regs[i].desc);
   }
}


static void dumpall(void)
{
   int i;
   int n=sizeof(regs)/sizeof(struct reg_info);

   for (i=0; i<n; i++) {
      dumpentry(i);
   }
}


static void dumpmatching(char *name)
{
   int i;
   int n=sizeof(regs)/sizeof(struct reg_info);


   for (i=0; i<n; i++) {
      if (strstr(regs[i].name, name))
         dumpentry(i);
   }
}


static void setreg(char *name, u32 val)
{
   int i;
   u32 mem;
   int found=0;
   int count=0;
   int n=sizeof(regs)/sizeof(struct reg_info);


   for (i=0; i<n; i++) {
      if (strcmp(regs[i].name, name)==0) {
         found = i;
         //printf("Matched %s with %s, count=%d\n", regs[i].name, name, count);
         count++;
      }
   }
   if (count!=1) {
      printf("No or more than one matching register found\n");
      exit(1);
   }

   mem = getmem(regs[found].addr);
   //printf("Old contents: 0x%08x\n", mem);
   mem &= ~(regs[found].mask << regs[found].shift);
   //printf("Unmasked contents: 0x%08x\n", mem);
   val &= regs[found].mask;
   //printf("mask: 0x%08x\n", regs[found].mask);
   //printf("masked val: 0x%08x\n", val);
   mem |= val << regs[found].shift;
   //printf("Embedded value: 0x%08x\n", mem);
   putmem(regs[found].addr, mem);
}


int main(int argc, char *argv[])
{
    char *p;
    u32 val;

    if (argc == 1) {
       dumpall();
       return 0;
    }

    // Uppercase first argument
    if (argc >= 2) {
        p = argv[1];
        while (*p) {
           *p = toupper(*p);
           p++;
        }
    }

    if (argc == 2) {
       dumpmatching(argv[1]);
       return 0;
    }

    if (argc == 3) {
	sscanf(argv[2],"%i",&val);
	setreg(argv[1], val);
	return 0;
    }

    printf("Usage: %s                - to dump all known registers\n"
           "       %s <name>         - to dump named register\n"
           "       %s <name> <value> - to set named register\n",
           argv[0], argv[0], argv[0]);
    return 1;
}
