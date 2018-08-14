do_install_append() {
	# The mouse being autoenabled is just annoying in xfce4-terminal (mouse
	# drag make vim go into visual mode and there is no right click menu),
	# delete the block.
	sed -i '/the mouse works just fine/,+4d' ${D}/${datadir}/${BPN}/vimrc
}
