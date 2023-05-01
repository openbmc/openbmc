SRC_URI += "\
    git://github.com/gtk-rs/glib;protocol=https;nobranch=1;name=glib;destsuffix=glib;type=git-dependency \
    git://github.com/gtk-rs/sys;protocol=https;nobranch=1;name=glib-sys;destsuffix=glib-sys;subpath=glib-sys;type=git-dependency \
    git://github.com/gtk-rs/sys;protocol=https;nobranch=1;name=gobject-sys;destsuffix=gobject-sys;subpath=gobject-sys;type=git-dependency \
"

SRCREV_FORMAT .= "_glib"
SRCREV_glib = "c9ee583cea07830c099cdcccd33eda9ef705ea93"

SRCREV_FORMAT .= "_glib-sys"
SRCREV_glib-sys = "5f35e26c65d24f8f018f643218de0f5807ba5f01"

SRCREV_FORMAT .= "_gobject-sys"
SRCREV_gobject-sys = "5f35e26c65d24f8f018f643218de0f5807ba5f01"
