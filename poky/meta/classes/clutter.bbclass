def get_minor_dir(v):
    import re
    m = re.match(r"^([0-9]+)\.([0-9]+)", v)
    return "%s.%s" % (m.group(1), m.group(2))

def get_real_name(n):
    import re
    m = re.match(r"^([a-z]+(-[a-z]+)?)(-[0-9]+\.[0-9]+)?", n)
    return "%s" % (m.group(1))

VERMINOR = "${@get_minor_dir("${PV}")}"
REALNAME = "${@get_real_name("${BPN}")}"

SRC_URI = "${GNOME_MIRROR}/${REALNAME}/${VERMINOR}/${REALNAME}-${PV}.tar.xz;name=archive"
S = "${WORKDIR}/${REALNAME}-${PV}"

inherit autotools pkgconfig gtk-doc gettext
