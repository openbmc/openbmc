# RaspberryPi BSP default settings

IMAGE_CLASSES += "sdcard_image-rpi"

# RPI kernel has errors of its own which should be filtered
# therefore use parselogs_rpi test instead of parselogs from oe-core
#
DEFAULT_TEST_SUITES:append:rpi = " parselogs_rpi"
DEFAULT_TEST_SUITES:remove:rpi = " parselogs"
