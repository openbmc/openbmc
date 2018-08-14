EXTRA_OECONF_append_rpi = " CPPFLAGS='-I${STAGING_INCDIR}/interface/vcos/pthreads \
                                   -I${STAGING_INCDIR}/interface/vmcs_host/linux'"

# if using bcm driver enable dispmanx not when using VC4 driver

PACKAGECONFIG_append_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' dispmanx', d)}"

PACKAGECONFIG_GL_rpi = "egl gles2"

PACKAGECONFIG_append_rpi = " hls libmms faad"

PACKAGECONFIG[dispmanx] = "--enable-dispmanx,--disable-dispmanx,userland"
