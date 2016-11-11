RDEPENDS_${PN} += "python-resource"
do_install_append_class-nativesdk() {
        sed -i -e 's|^#!.*/usr/bin/env python|#! /usr/bin/env python|' ${D}${bindir}/mako-render
}
