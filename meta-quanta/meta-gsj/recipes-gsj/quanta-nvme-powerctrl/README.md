### NVME SSD Power Control Manager

#### Description
    The package mantains for SSD power control and related noification handle Deamon

#### Design

     The service power supply design follow PCI Express Card Electromechanical Specification Revision 3.0 in Section 2.2

     nvme_gpio.service follow section 2.2.1 Initial power-Up to initinal and adjust related signal.

     nvme_powermanager.service follow section 2.2.2 power management states monitor PRESENT signal and update related signal.

#### Process

    * Plugging
        1. U2_[SSD_index] PRSNT_N will be input low
        2. Set PWR_U2_[SSD_index]_EN to high
        3. Check PWRGD_U2_[SSD_index] is high
        4-1. If PWRGD_U2_[SSD_index] is high (PWR Good)
		    - Wait 5ms
		    - Enable PCI Clock by SMBus 9FGL0851
		    - Wait 100ms
            - Set RST_BMC_U2 to high

        4-2. If PWRGD_U2_[SSD_index] is low (PWR Fail)
		    - Set RST_BMC_U2_[SSD_index]_R_N to low
            - Wait 100ms
            - Disable PCI Clock by SMBus

    * Removing
        1. U2_[SSD_index] PRSNT_N will be input high
        2. Set RST_BMC_U2 to low
        3. Wait 100ms
        4. Disable PCI Clock by SMBus
        5. Wait 5ms
        6. PWR_U2_[SSD_index]_EN to low

#### Test

    1. PRESENT detect SSD: The hardware design has been implemented.
    2. Initial SSD slot Power output: nvme_gpio service has tested on Module. It could sucess initial gpios and set correct power output.
    3. Detect PRESENT and change power setting: nvme_powermanager service has tested on Module. It could success detect SSD plugged or removal change power output.
    4. Improve initial power-up sequence: For matched hardware default initial power-up setting, the nvme_gpio service only set unplugged slot related signal.
    5. Improve service execute sequence: nvme_powermanager.service must wait for nvme_gpio.service ensure gpio export complete.

