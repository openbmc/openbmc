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

3. Overwrite SDIMG_COMPRESSION in local.conf
    * `SDIMG_COMPRESSION = "xz"`

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

## Add purchased license codecs

To add you own licenses use variables `KEY_DECODE_MPG2` and `KEY_DECODE_WVC1` in
local.conf. Example:

    KEY_DECODE_MPG2 = "12345678"
    KEY_DECODE_WVC1 = "12345678"

You can supply more licenses separated by comma. Example:

    KEY_DECODE_WVC1 = "0x12345678,0xabcdabcd,0x87654321"


## Disable overscan

By default the GPU adds a black border around the video output to compensate for
TVs which cut off part of the image. To disable this set this variable in
local.conf:

    DISABLE_OVERSCAN = "1"

## Set overclocking options

The Raspberry PI can be overclocked. As of now overclocking up to the "Turbo
Mode" is officially supported by the raspbery and does not void warranty. Check
the config.txt for a detailed description of options and modes. Example turbo
mode:

    ARM_FREQ = "1000"
    CORE_FREQ = "500"
    SDRAM_FREQ = "500"
    OVER_VOLTAGE = "6"

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

## Boot to U-Boot

To have u-boot load kernel image, set in your local.conf:

    KERNEL_IMAGETYPE = "uImage"

This will make kernel.img be u-boot image which will load uImage. By default,
kernel.img is the actual kernel image (ex. Image).

## Image with Initramfs

To build an initramfs image:

* Set this 3 kernel variables (in linux-raspberrypi.inc for example)
  - kernel_configure_variable BLK_DEV_INITRD y
  - kernel_configure_variable INITRAMFS_SOURCE ""
  - kernel_configure_variable RD_GZIP y

* Set the yocto variables (in linux-raspberrypi.inc for example)
  - `INITRAMFS_IMAGE = "<a name for your initramfs image>"`
  - `INITRAMFS_IMAGE_BUNDLE = "1"`

* Set the meta-rasberrypi variable (in raspberrypi.conf for example)
  - `KERNEL_INITRAMFS = "-initramfs"`

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
RaspberryPi3 will have to explicitely set in local.conf:

    ENABLE_UART = "1"

Ref.:
* <https://github.com/raspberrypi/firmware/issues/553>
* <https://github.com/RPi-Distro/repo/issues/22>
