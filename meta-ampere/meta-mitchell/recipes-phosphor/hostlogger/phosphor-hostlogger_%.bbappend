FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
                file://ttyS0.conf \
                file://ttyS1.conf \
                file://ttyS2.conf \
                file://ttyS3.conf \
                file://ttyS7.conf \
                file://ttyS8.conf \
                "

do_install:append() {

          # Install the configurations
          install -m 0755 -d ${D}${sysconfdir}/${BPN}
          install -m 0644 ${UNPACKDIR}/*.conf ${D}${sysconfdir}/${BPN}/

          # Remove upstream-provided default configuration
          rm -f ${D}${sysconfdir}/${BPN}/ttyVUART0.conf
}
