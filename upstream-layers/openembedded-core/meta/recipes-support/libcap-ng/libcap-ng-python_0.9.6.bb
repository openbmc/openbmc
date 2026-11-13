require libcap-ng.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/libcap-ng:"

SUMMARY .= " - python"

inherit python3targetconfig

DEPENDS += "libcap-ng python3 swig-native"

EXTRA_OECONF += "--with-python3"

do_install() {
    oe_runmake 'DESTDIR=${D}' install -C ${B}/bindings/python3
}

FILES:${PN} = "${libdir}/python${PYTHON_BASEVERSION}"
