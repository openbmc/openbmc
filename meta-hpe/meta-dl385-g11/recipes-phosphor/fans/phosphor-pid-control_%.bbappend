FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
SRC_URI:append = " file://config.json"

FILES:${PN} = "${datadir}/swampd/config.json /usr/share/swampd/* /usr/bin/swampd /usr/bin/setsensor"

do_install:append(){
  install -d ${D}${datadir}/swampd
  install -m 0644 -D ${UNPACKDIR}/config.json ${D}${datadir}/swampd/
}
