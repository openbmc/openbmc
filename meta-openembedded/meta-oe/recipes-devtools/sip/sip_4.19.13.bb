require sip.inc

DEPENDS = "python"

inherit python-dir pythonnative

PACKAGES += "python-sip"

FILES_python-sip = "${libdir}/${PYTHON_DIR}/site-packages/"
FILES_${PN}-dbg += "${libdir}/${PYTHON_DIR}/site-packages/.debug"

