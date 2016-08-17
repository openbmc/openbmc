HOMEPAGE = "http://www.enlightenment.org"
SRCNAME ?= "${BPN}"
SRCVER ?= "${PV}"

EFL_SRCREV ?= "82070"

ARM_INSTRUCTION_SET = "arm"

S = "${WORKDIR}/${SRCNAME}-${SRCVER}"

# each efl or e17 module is installing module.so in different directory, disable shlibs providers for them
# evas-engine-gl-x11-1.7.7 evas-engine-software-16-1.7.7 evas-engine-software-16-x11-1.7.7 evas-engine-software-x11-1.7.7 evas-engine-wayland-shm-1.7.7 evas-cserve2-bmp-1.7.7 evas-cserve2-eet-1.7.7 evas-cserve2-ico-1.7.7 evas-cserve2-jpeg-1.7.7 evas-cserve2-pmaps-1.7.7 evas-cserve2-png-1.7.7 evas-cserve2-psd-1.7.7 evas-cserve2-tga-1.7.7 evas-cserve2-tiff-1.7.7 evas-cserve2-wbmp-1.7.7 evas-cserve2-xpm-1.7.7 evas-loader-bmp-1.7.7 evas-loader-generic-1.7.7 evas-loader-gif-1.7.7 evas-loader-ico-1.7.7 evas-loader-jpeg-1.7.7 evas-loader-pmaps-1.7.7 evas-loader-png-1.7.7 evas-loader-psd-1.7.7 evas-loader-tga-1.7.7 evas-loader-tiff-1.7.7 evas-loader-wbmp-1.7.7 evas-loader-xpm-1.7.7 evas-saver-jpeg-1.7.7 evas-saver-png-1.7.7 evas-saver-tiff-1.7.7 evas-engine-fb-1.7.7
# emotion-1.7.7 elementary-1.7.7 elementary-tests-1.7.7 elementary-accessibility-1.7.7 e-wm-0.17.3 elfe-0.0.1+svnr82070 shr-e-gadgets-0.0.0+gitr1+27b6c17d73 cpu-0.0.1+svnr82070 places-0.1.0+svnr82070 forecasts-0.2.0+svnr82070 uptime-0.0.2+svnr82070 screenshot-0.3.0+svnr82070 exalt-client-0.0.1+svnr82070 diskio-0.0.1+svnr82070 rain-0.0.3+svnr82070 news-0.1.0+svnr82070 flame-0.0.3+svnr82070
PRIVATE_LIBS = "module.so"
