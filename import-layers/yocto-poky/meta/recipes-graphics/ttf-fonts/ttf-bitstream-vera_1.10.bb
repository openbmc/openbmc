SUMMARY = "The Bitstream Vera fonts - TTF Edition"
DESCRIPTION = "The Bitstream Vera fonts include four monospace and sans \
faces (normal, oblique, bold, bold oblique) and two serif faces (normal \
and bold).  In addition Fontconfig/Xft2 can artificially oblique the \
serif faces for you: this loses hinting and distorts the faces slightly, \
but is visibly different than normal and bold, and reasonably pleasing."
SECTION = "x11/fonts"
LICENSE = "BitstreamVera"
LIC_FILES_CHKSUM = "file://COPYRIGHT.TXT;md5=27d7484b1e18d0ee4ce538644a3f04be"
PR = "r7"

inherit fontcache

FONT_PACKAGES = "${PN}"

SRC_URI = "${GNOME_MIRROR}/ttf-bitstream-vera/1.10/ttf-bitstream-vera-${PV}.tar.bz2" 

do_install () { 
        install -d ${D}${prefix}/share/fonts/ttf/ 
        for i in *.ttf; do 
                install -m 644 $i ${D}${prefix}/share/fonts/ttf/${i} 
        done 

	# fontconfig ships this too.  not sure what to do about it.
        #install -d ${D}${sysconfdir}/fonts 
        #install -m 644 local.conf ${D}${sysconfdir}/fonts/local.conf 


        install -d ${D}${prefix}/share/doc/${BPN}/
        for i in *.TXT; do 
                install -m 644 $i ${D}${prefix}/share/doc/${BPN}/$i
        done 
} 

FILES_${PN} = "/etc ${datadir}/fonts"

SRC_URI[md5sum] = "bb22bd5b4675f5dbe17c6963d8c00ed6"
SRC_URI[sha256sum] = "db5b27df7bbb318036ebdb75acd3e98f1bd6eb6608fb70a67d478cd243d178dc"
