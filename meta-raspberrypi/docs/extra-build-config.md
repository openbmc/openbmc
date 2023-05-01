# Optional build configuration

There are a set of ways in which a user can influence different parameters of
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

See: <https://www.raspberrypi.com/documentation/computers/config_txt.html#memory-options>

## VC4

By default, each machine uses `vc4` for graphics. This will in turn sets mesa as provider for `gl` libraries. `DISABLE_VC4GRAPHICS` can be set to `1` to disable this behaviour falling back to using `userland`. Be aware that `userland` has not support for 64-bit arch. If you disable `vc4` on a 64-bit Raspberry Pi machine, expect build breakage.

## Add purchased license codecs

To add your own licenses use variables `KEY_DECODE_MPG2` and `KEY_DECODE_WVC1` in
local.conf. Example:

    KEY_DECODE_MPG2 = "12345678"
    KEY_DECODE_WVC1 = "12345678"

You can supply more licenses separated by comma. Example:

    KEY_DECODE_WVC1 = "0x12345678,0xabcdabcd,0x87654321"

See: <https://www.raspberrypi.com/documentation/computers/config_txt.html#licence-key-and-codec-options>

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
Mode" is officially supported by the Raspberry Pi and does not void warranty. Check
the config.txt for a detailed description of options and modes. The following
variables are supported in local.conf: `ARM_FREQ`, `GPU_FREQ`, `CORE_FREQ`,
`SDRAM_FREQ` and `OVER_VOLTAGE`.

Example official settings for Turbo Mode in Raspberry Pi 2:

    ARM_FREQ = "1000"
    CORE_FREQ = "500"
    SDRAM_FREQ = "500"
    OVER_VOLTAGE = "6"

See: <https://www.raspberrypi.com/documentation/computers/config_txt.html#overclocking-options>

## HDMI and composite video options

The Raspberry Pi can output video over HDMI or SDTV composite (the RCA connector).
By default the video mode for these is autodetected on boot: the HDMI mode is
selected according to the connected monitor's EDID information and the composite
mode is defaulted to NTSC using a 4:3 aspect ratio. Check the config.txt for a
detailed description of options and modes. The following variables are supported in
local.conf: `HDMI_FORCE_HOTPLUG`, `HDMI_DRIVE`, `HDMI_GROUP`, `HDMI_MODE`,
`HDMI_CVT`, `CONFIG_HDMI_BOOST`, `SDTV_MODE`, `SDTV_ASPECT` and `DISPLAY_ROTATE`.

Example to force HDMI output to 720p in CEA mode:

    HDMI_GROUP = "1"
    HDMI_MODE = "4"

See: <https://www.raspberrypi.com/documentation/computers/configuration.html#hdmi-configuration>

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

* Set this 3 kernel variables (in kernel's do_configure:prepend in linux-raspberrypi.inc after the line kernel_configure_variable LOCALVERSION "\"\""
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

## Including additional files in the SD card image boot partition

The SD card image class supports adding extra files into the boot
partition, where the files are copied from either the image root
partition or from the build image deploy directory.

To copy files that are present in the root partition into boot,
FATPAYLOAD is a simple space-separated list of files to be copied:

    FATPAYLOAD = "/boot/example1 /boot/example2"

To copy files from the image deploy directory, the files should be
listed in the DEPLOYPAYLOAD as a space-separated list of entries.
Each entry lists a file to be copied, and an optional destination
filename can be specified by supplying it after a colon separator.

    DEPLOYPAYLOAD = "example1-${MACHINE}:example1 example2"

Files that are to be included from the deploy directory will be produced
by tasks that image building task must depend upon, to ensure that the
files are available when they are needed, so these component deploy
tasks must be added to: RPI_SDIMG_EXTRA_DEPENDS.

    RPI_SDIMG_EXTRA_DEPENDS:append = " example:do_deploy"

## Enable SPI bus

When using device tree kernels, set this variable to enable the SPI bus:

    ENABLE_SPI_BUS = "1"

## Enable I2C

When using device tree kernels, set this variable to enable I2C:

    ENABLE_I2C = "1"

Furthermore, to auto-load I2C kernel modules set:

    KERNEL_MODULE_AUTOLOAD:rpi += "i2c-dev i2c-bcm2708"

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
* pitft28c
* pitft35r

## Misc. display

If you would like to use the Waveshare "C" 1024×600, 7 inch Capacitive Touch
Screen LCD, HDMI interface (<http://www.waveshare.com/7inch-HDMI-LCD-C.htm>) Rev
2.1, please set the following in your local.conf:

    WAVESHARE_1024X600_C_2_1 = "1"

## Enable UART

RaspberryPi 0, 1, 2 and CM will have UART console enabled by default.

RaspberryPi 0 WiFi and 3 does not have the UART enabled by default because this
needs a fixed core frequency and enable_uart will set it to the minimum. Certain
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

## Enable USB host support

By default in case of the Compute Module 4 IO Board the standard USB driver
that usually supports host mode operations is disabled for power saving reasons.
Users who want to use the 2 USB built-in ports or the other ports provided via
the header extension should set the following in local.conf:

    ENABLE_DWC2_HOST = "1"

## Set CPUs to be isolated from the standard Linux scheduler

By default Linux will use all available CPUs for scheduling tasks. For real time
purposes there can be an advantage to isolating one or more CPUs from the
standard scheduler. It should be noted that CPU 0 is special, it is the only CPU
available during the early stages of the boot process and cannot be isolated.

The string assigned to this variable may be a single CPU number, a comma
separated list ("1,2"), a range("1-3"), or a mixture of these ("1,3-5")

    ISOLATED_CPUS = "1-2"

## Enable Openlabs 802.15.4 radio module

When using device tree kernels, set this variable to enable the 802.15.4 hat:

    ENABLE_AT86RF = "1"

See: <https://openlabs.co/OSHW/Raspberry-Pi-802.15.4-radio>

## Enable CAN

In order to use CAN with an MCP2515-based module, set the following variables:

    ENABLE_SPI_BUS = "1"
    ENABLE_CAN = "1"

In case of dual CAN module (e.g. PiCAN2 Duo), set following variables instead:

    ENABLE_SPI_BUS = "1"
    ENABLE_DUAL_CAN = "1"

Some modules may require setting the frequency of the crystal oscillator used on the particular board. The frequency is usually marked on the package of the crystal. By default, it is set to 16 MHz. To change that to 8 MHz, the following variable also has to be set:

    CAN_OSCILLATOR="8000000"

Tested modules:

* PiCAN2 (16 MHz crystal): <http://skpang.co.uk/catalog/pican2-canbus-board-for-raspberry-pi-23-p-1475.html>
* WaveShare RS485 CAN HAT (8 MHz or 12 MHz crystal): <https://www.waveshare.com/rs485-can-hat.htm>
* PiCAN2 Duo (16 MHz crystal): <http://skpang.co.uk/catalog/pican2-duo-canbus-board-for-raspberry-pi-23-p-1480.html>

## Enable infrared

Users who want to enable infrared support, for example for using LIRC (Linux
Infrared Remote Control), have to explicitly set in local.conf:

    ENABLE_IR = "1"

This will add device tree overlays gpio-ir and gpio-ir-tx to config.txt.
Appropriate kernel modules will be also included in the image. By default the
GPIO pin for gpio-ir is set to 18 and the pin for gpio-ir-tx is 17. Both pins
can be easily changed by modifying variables `GPIO_IR` and `GPIO_IR_TX`.

## Enable gpio-shutdown

When using device tree kernels, set this variable to enable gpio-shutdown:

    ENABLE_GPIO_SHUTDOWN = "1"

This will add the corresponding device tree overlay to config.txt and include
the gpio-keys kernel module in the image. If System V init is used, additional
mapping is applied to bind the button event to shutdown command. Systemd init
should handle the event out of the box.

By default the feature uses gpio pin 3 (except RPi 1 Model B rev 1 enumerates
the pin as gpio 1). This conflicts with the I2C bus. If you set `ENABLE_I2C`
to `1` or enabled `PiTFT` support, or otherwise want to use another pin, use
`GPIO_SHUTDOWN_PIN` to assign another pin. Example using gpio pin 25:

     GPIO_SHUTDOWN_PIN = "25"

## Enable One-Wire Interface

One-wire is a single-wire communication bus typically used to connect sensors
to the RaspberryPi. The Raspberry Pi supports one-wire on any GPIO pin, but
the default is GPIO 4. To enable the one-wire interface explicitly set it in
`local.conf`

    ENABLE_W1 = "1"

Once discovery is complete you can list the devices that your Raspberry Pi has
discovered via all 1-Wire busses check the interface with this command

`ls /sys/bus/w1/devices/`

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
## Enable Raspberry Pi Camera Module

Raspberry Pi does not have the unicam device ( Raspberry Pi Camera ) enabled by default.
Because this unicam device ( bcm2835-unicam ) as of now is used by libcamera opensource.
So we have to explicitly enable it in local.conf.

    RASPBERRYPI_CAMERA_V2 = "1"

This will add the device tree overlay imx219 ( Raspberry Pi Camera Module V2 sensor driver 
) to config.txt. Also, this will enable adding Contiguous Memory Allocation value in the 
cmdline.txt.

Similarly, the Raspberry Pi Camera Module v3 also has to be explicitly enabled in local.conf.

    RASPBERRYPI_CAMERA_V3 = "1"

This will add the device tree overlay imx708 ( Raspberry Pi Camera Module V3 sensor driver ) 
to config.txt.

See:
* <https://www.raspberrypi.com/documentation/computers/camera_software.html>
* <https://www.raspberrypi.org/blog/an-open-source-camera-stack-for-raspberry-pi-using-libcamera/>

## WM8960 soundcard support

Support for WM8960 based sound cards such as the WM8960 Hi-Fi Sound Card HAT for Raspberry Pi from Waveshare, and ReSpeaker 2 / 4 / 6 Mics Pi HAT from Seeed Studio, can be enabled in `local.conf`

    MACHINE_FEATURES += "wm8960"

You may need to adjust volume and toggle switches that are off by default

    amixer -c1 sset 'Headphone',0 80%,80%
    amixer -c1 sset 'Speaker',0 80%,80%
    amixer -c1 sset 'Left Input Mixer Boost' toggle
    amixer -c1 sset 'Left Output Mixer PCM' toggle
    amixer -c1 sset 'Right Input Mixer Boost' toggle
    amixer -c1 sset 'Right Output Mixer PCM' toggle

Audio capture on ReSpeaker 2 / 4 / 6 Mics Pi HAT from Seeed Studio is very noisy.

## Support for RTC devices

The RaspberryPi boards don't feature an RTC module and the machine
configurations provided in this BSP layer have this assumption (until, if at
all, some later boards will come with one).

`rtc` is handled as a `MACHINE_FEATURES` in the context of the build system
which means that if an attached device is provided for which support is needed,
the recommended way forward is to write a new machine configuration based on an
existing one. Check the documentation for
`MACHINE_FEATURES_BACKFILL_CONSIDERED` for how this is disabled for the
relevant machines.

Even when `MACHINE_FEATURES` is tweaked to include the needed `rtc` string,
make sure that your kernel configuration is supporting the attached device and
the device tree is properly tweaked. Also, mind the runtime components that
take advantage of your RTC device. You can do that by checking what is
included/configured in the build system based on the inclusion of `rtc` in
`MACHINE_FEATURES`.

## Raspberry Pi Distro VLC

To enable Raspberry Pi Distro VLC, the `meta-openembedded/meta-multimedia` layer must be
included in your `bblayers.conf`.

VLC does not support HW accelerated video decode through MMAL on a 64-bit OS.

See:
* <https://forums.raspberrypi.com/viewtopic.php?t=275370>
* <https://forums.raspberrypi.com/viewtopic.php?t=325218#p1946169>

MMAL is not enabled by default. To enable it add

    DISABLE_VC4GRAPHICS = "1"

to `local.conf`. Adding `vlc` to `IMAGE_INSTALL` will then default to building the Raspberry
Pi's Distro implementation of VLC with HW accelerated video decode through MMAL into the system
image. It also defaults to building VLC with Raspberry PI's Distro implementation of ffmpeg. The
oe-core implementation of ffmpeg and the meta-openembedded/meta-multimedia implementation of VLC
can however be selected via:

    PREFERRED_PROVIDER_ffmpeg = "ffmpeg"
    PREFERRED_PROVIDER_vlc = "vlc"

Usage example: Start VLC with mmal_vout plugin and without an active display server.

    DISPLAYNUM=$(tvservice -l | tail -c 2)
    MMAL_DISPLAY=$(expr $DISPLAYNUM + 1)
    VLC_SETTINGS="-I dummy --vout=mmal_vout --mmal-resize --mmal-display hdmi-$MMAL_DISPLAY --no-dbus"
    cvlc $VLC_SETTINGS <video/playlist>
