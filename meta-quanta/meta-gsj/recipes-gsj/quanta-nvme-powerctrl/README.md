### NVME SSD Power Control Manager

#### Description
    The Package is Mantain for SSD power control and related noification handle Deamon

#### Design
     nvme_gpio.service for initinal related GPIOs ( PRESENT, PWR, PWRGD, RST_U2 ) and according PRESENT signal fix HW PWR default output signal. 

     nvme_powermanager.service loop for monitor PRESENT signal and update PWR output.

#### Process

    * Plugging 
        1. U2_[SSD_index]PRSNT_N will be input low
        2. Set PWR_U2_[SSD_index]_EN to high
        3. Check PWRGD_U2_[SSD_index] is high
        4-1. If PWRGD_U2_[SSD_index] is high (PWR Good)
		    - Wait 5ms
		    - Enable PCI Clock by SMBus 9FGL0851
		    - Wait 100ms
            - Reset RST_BMC_U2 [low->high]
		    - Send Assert to tell package could read SSD status.

        4-2. If PWRGD_U2_[SSD_index] is low (PWR Fail)
		    - Disable Clock by SMBus
		    - Wait 100ms
		    - Set RST_BMC_U2_[SSD_index]_R_N to low
		    - Set LED_U2_FAULT to high

    * Removing
        1. U2_[SSD_index]PRSNT_N will be input high
        2. Disable Clock by SMBus 9FGL0851
        3. PWR_U2_[SSD_index]_EN to low
        4. Check PWRGD_U2_[SSD_index] is low
        5. Send Assert to tell package update SSD status.


#### TODO
    
    1. After power on ssd, bmc  send command to 9FGL0851 (on SMBus8 0x68) for enable PCI clock, and send RST_BMC_U2 pin rsing event (set low, wait 500ms, set high). The Host could rescan PCI and detect SSD device.

#### Test

    1. PRESENT detect SSD: The Default Hardware is implement.
    2. Initial SSD slot Power output: nvme_gpio service has test on Module. It could sucess initial gpios and setting correct power output.
    3. Detect PRESENT and change power setting: nvme_powermanager service has tested on Module. It could success detect SSD plugged or removal change PWR output.
    4. NVME-MI connect: Ensure SSD power is ready then it will notify quanta NVME-MI package could read SSD information. 
    5. FAULT LED handle: When detect power output irregular by PWRGD pin, the service will trigger related FAULT LED.


