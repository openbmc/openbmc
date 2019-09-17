# Optional build configuration

There are a set of ways in which a user can influence different paramenters of
the build. We list here the ones that are closely related to this BSP or
specific to it. For the rest please check:
<http://www.yoctoproject.org/docs/latest/ref-manual/ref-manual.html>

## Compressed deployed files

1. Overwrite IMAGE_FSTYPES in local.conf
    * `IMAGE_FSTYPES = "tar.bz2 ext3.xz"`

2. Overwrite SDIMG_ROOTFS_TYPE in local.conf
    * `SDIMG_ROOTFS_TYPE = "ext3.xz"`

Accommodate the values above to your own needs (ex: ext3 / ext4).

## GPU memory

* `GPU_MEM`: GPU memory in megabyte. Sets the memory split between the ARM and
  GPU. ARM gets the remaining memory. Min 16. Default 64.

* `GPU_MEM_256`: GPU memory in megabyte for the 256MB Raspberry Pi. Ignored by
  the 512MB RP. Overrides gpu_mem. Max 192. Default not set.

* `GPU_MEM_512`: GPU memory in megabyte for the 512MB Raspberry Pi. Ignored by
  the 256MB RP. Overrides gpu_mem. Max 448. Default not set.

* `GPU_MEM_1024`: GPU memory in megabyte for the 1024MB Raspberry Pi. Ignored by
  the 256MB/512MB RP. Overrides gpu_mem. Max 944. Default not set.

See: <https://www.raspberrypi.org/documentation/configuration/config-txt/memory.md>

## VC4

By default, each machine uses `vc4` for graphics. This will in turn sets mesa as provider for `gl` libraries. `DISABLE_VC4GRAPHICS` can be set to `1` to disable this behaviour falling back to using `userland`. Be aware that `userland` has not support for 64-bit arch. If you disable `vc4` on a 64-bit Raspberry Pi machine, expect build breakage.

## Add purchased license codecs

To add you own licenses use variables `KEY_DECODE_MPG2` and `KEY_DECODE_WVC1` in
local.conf. Example:

    KEY_DECODE_MPG2 = "12345678"
    KEY_DECODE_WVC1 = "12345678"

You can supply more licenses separated by comma. Example:

    KEY_DECODE_WVC1 = "0x12345678,0xabcdabcd,0x87654321"

See: <https://www.raspberrypi.org/documentation/configuration/config-txt/codeclicence.md>

## Disable overscan

By default the GPU adds a black border around the video output to compensate for
TVs which cut off part of the image. To disable this set this variable in
local.conf:

    DISABLE_OVERSCAN = "1"

## Disable splash screen

By default a rainbow splash screen is shown after the GPU firmware is loaded.
To disable this set this variable in local.conf:

    DISABLE_SPLASH = "1"

## Boot delay

The Raspberry Pi waits a number of seconds after loading the GPU firmware and
before loading the kernel. By default it is one second. This is useful if your
SD card needs a while to get ready before Linux is able to boot from it.
To remove (or adjust) this delay set these variables in local.conf:

    BOOT_DELAY = "0"
    BOOT_DELAY_MS = "0"

## Set overclocking options

The Raspberry Pi can be overclocked. As of now overclocking up to the "Turbo
Mode" is officially supported by the raspbery and does not void warranty. Check
the config.txt for a detailed description of options and modes. The following
variables are supported in local.conf: `ARM_FREQ`, `GPU_FREQ`, `CORE_FREQ`,
`SDRAM_FREQ` and `OVER_VOLTAGE`.

Example official settings for Turbo Mode in Raspberry Pi 2:

    ARM_FREQ = "1000"
    CORE_FREQ = "500"
    SDRAM_FREQ = "500"
    OVER_VOLTAGE = "6"

See: <https://www.raspberrypi.org/documentation/configuration/config-txt/overclocking.md>

## HDMI and composite video options

The Raspberry Pi can output video over HDMI or SDTV composite (the RCA connector).
By default the video mode for these is autodetected on boot: the HDMI mode is
selected according to the connected monitor's EDID information and the composite
mode is defaulted to NTSC using a 4:3 aspect ratio. Check the config.txt for a
detailed description of options and modes. The following variables are supported in
local.conf: `HDMI_FORCE_HOTPLUG`, `HDMI_DRIVE`, `HDMI_GROUP`, `HDMI_MODE`,
`CONFIG_HDMI_BOOST`, `SDTV_MODE`, `SDTV_ASPECT` and `DISPLAY_ROTATE`.

Example to force HDMI output to 720p in CEA mode:

    HDMI_GROUP = "1"
    HDMI_MODE = "4"

See: <https://www.raspberrypi.org/documentation/configuration/config-txt/video.md>

## Video camera support with V4L2 drivers

Set this variable to enable support for the video camera (Linux 3.12.4+
required):

    VIDEO_CAMERA = "1"

## Enable offline compositing support

Set this variable to enable support for dispmanx offline compositing:

    DISPMANX_OFFLINE = "1"

This will enable the firmware to fall back to off-line compositing of Dispmanx
elements. Normally the compositing is done on-line, during scanout, but cannot
handle too many elements. With off-line enabled, an off-screen buffer is
allocated for compositing. When scene complexity (number and sizes
of elements) is high, compositing will happen off-line into the buffer.

Heavily recommended for Wayland/Weston.

See: <http://wayland.freedesktop.org/raspberrypi.html>

## Enable kgdb over console support

To add the kdbg over console (kgdboc) parameter to the kernel command line, set
this variable in local.conf:

    ENABLE_KGDB = "1"

## Disable rpi boot logo

To disable rpi boot logo, set this variable in local.conf:

    DISABLE_RPI_BOOT_LOGO = "1"

## Boot to U-Boot

To have u-boot load kernel image, set in your local.conf:

    RPI_USE_U_BOOT = "1"

This will select the appropriate image format for use with u-boot automatically.
For further customisation the KERNEL_IMAGETYPE and KERNEL_BOOTCMD variables can
be overridden to select the exact kernel image type (eg. zImage) and u-boot
command (eg. bootz) to be used.

## Image with Initramfs

To build an initramfs image:

* Set this 3 kernel variables (in kernel's do_configure_prepend in linux-raspberrypi.inc after the line kernel_configure_variable LOCALVERSION "\"\""
)
  - kernel_configure_variable BLK_DEV_INITRD y
  - kernel_configure_variable INITRAMFS_SOURCE ""
  - kernel_configure_variable RD_GZIP y

* Set the yocto variables (e.g. in local.conf)
  - `INITRAMFS_IMAGE = "<name for your initramfs image>"`
  - `INITRAMFS_IMAGE_BUNDLE = "1"`
  - `BOOT_SPACE = "1073741"`
  - `INITRAMFS_MAXSIZE = "315400"`
  - `IMAGE_FSTYPES_pn-${INITRAMFS_IMAGE} = "${INITRAMFS_FSTYPES}"`

## Enable SPI bus

When using device tree kernels, set this variable to enable the SPI bus:

    ENABLE_SPI_BUS = "1"

## Enable I2C

When using device tree kernels, set this variable to enable I2C:

    ENABLE_I2C = "1"

## Enable PiTFT support

Basic support for using PiTFT screens can be enabled by adding below in
local.conf:

* `MACHINE_FEATURES += "pitft"`
  - This will enable SPI bus and i2c device-trees, it will also setup
    framebuffer for console and x server on PiTFT.

NOTE: To get this working the overlay for the PiTFT model must be build, added
and specified as well (dtoverlay=<driver> in config.txt).

Below is a list of currently supported PiTFT models in meta-raspberrypi, the
modelname should be added as a MACHINE_FEATURES in local.conf like below:

    MACHINE_FEATURES += "pitft <modelname>"

List of currently supported models:
* pitft22
* pitft28r
* pitft35r

## Misc. display

If you would like to use the Waveshare "C" 1024×600, 7 inch Capacitive Touch
Screen LCD, HDMI interface (<http://www.waveshare.com/7inch-HDMI-LCD-C.htm>) Rev
2.1, please set the following in your local.conf:

    WAVESHARE_1024X600_C_2_1 = "1"

## Enable UART

RaspberryPi 0, 1, 2 and CM will have UART console enabled by default.

RaspberryPi 0 WiFi and 3 does not have the UART enabled by default because this
needs a fixed core frequency and enable_uart wil set it to the minimum. Certain
operations - 60fps h264 decode, high quality deinterlace - which aren't
performed on the ARM may be affected, and we wouldn't want to do that to users
who don't want to use the serial port. Users who want serial console support on
RaspberryPi 0 Wifi or 3 will have to explicitly set in local.conf:

    ENABLE_UART = "1"

Ref.:
* <https://github.com/raspberrypi/firmware/issues/553>
* <https://github.com/RPi-Distro/repo/issues/22>

## Enable USB Peripheral (Gadget) support

The standard USB driver only supports host mode operations.  Users who
want to use gadget modules like g_ether should set the following in
local.conf:

    ENABLE_DWC2_PERIPHERAL = "1"

## Enable Openlabs 802.15.4 radio module

When using device tree kernels, set this variable to enable the 802.15.4 hat:

    ENABLE_AT86RF = "1"

See: <https://openlabs.co/OSHW/Raspberry-Pi-802.15.4-radio>

## Enable CAN with Pican2

In order to use Pican2 CAN module, set the following variables:

	ENABLE_SPI_BUS = "1"
	ENABLE_CAN = "1"

See: <http://skpang.co.uk/catalog/pican2-canbus-board-for-raspberry-pi-23-p-1475.html>

## Manual additions to config.txt

The `RPI_EXTRA_CONFIG` variable can be used to manually add additional lines to
the `config.txt` file if there is not a specific option above for the
configuration you need. To add multiple lines you must include `\n` separators.
If double-quotes are needed in the lines you are adding you can use single
quotes around the whole string.

For example, to add a comment containing a double-quote and a configuration
option:

    RPI_EXTRA_CONFIG = ' \n \
        # Raspberry Pi 7\" display/touch screen \n \
        lcd_rotate=2 \n \
        '
