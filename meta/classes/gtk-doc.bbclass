# Helper class to pull in the right gtk-doc dependencies and disable
# gtk-doc.
#
# Long-term it would be great if this class could be toggled between
# gtk-doc-stub-native and the real gtk-doc-native, which would enable
# re-generation of documentation.  For now, we'll make do with this which
# packages up any existing documentation (so from tarball builds).

# The documentation directory, where the infrastructure will be copied.
# gtkdocize has a default of "." so to handle out-of-tree builds set this to $S.
GTKDOC_DOCDIR ?= "${S}"

DEPENDS_append = " gtk-doc-stub-native"

EXTRA_OECONF_append = "\
  --disable-gtk-doc \
  --disable-gtk-doc-html \
  --disable-gtk-doc-pdf \
"

do_configure_prepend () {
	( cd ${S}; gtkdocize --docdir ${GTKDOC_DOCDIR} )
}

inherit pkgconfig
