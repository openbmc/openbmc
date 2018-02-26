LDSO_TCLIBC = "glibc"
LDSO_TCLIBC_libc-musl = "musl"
LDSO_TCLIBC_libc-baremetal = "musl"

linuxloader_glibc () {
	case ${TARGET_ARCH} in
		powerpc | microblaze )
			dynamic_loader="${base_libdir}/ld.so.1"
			;;
		mipsisa32r6el | mipsisa32r6 | mipsisa64r6el | mipsisa64r6)
			dynamic_loader="${base_libdir}/ld-linux-mipsn8.so.1"
			;;
		mips* )
			dynamic_loader="${base_libdir}/ld.so.1"
			;;
		powerpc64)
			dynamic_loader="${base_libdir}/ld64.so.1"
			;;
		x86_64)
			dynamic_loader="${base_libdir}/ld-linux-x86-64.so.2"
			;;
		i*86 )
			dynamic_loader="${base_libdir}/ld-linux.so.2"
			;;
		arm )
			dynamic_loader="${base_libdir}/ld-linux.so.3"
			;;
		* )
			dynamic_loader="/unknown_dynamic_linker"
			;;
	esac
	echo $dynamic_loader
}

linuxloader_musl () {
	case ${TARGET_ARCH} in
		microblaze* )
			dynamic_loader="${base_libdir}/ld-musl-microblaze${@bb.utils.contains('TUNE_FEATURES', 'bigendian', '', 'el' ,d)}.so.1"
			;;
		mips* )
			dynamic_loader="${base_libdir}/ld-musl-mips${ABIEXTENSION}${MIPSPKGSFX_BYTE}${MIPSPKGSFX_R6}${MIPSPKGSFX_ENDIAN}${@['', '-sf'][d.getVar('TARGET_FPU') == 'soft']}.so.1"
			;;
		powerpc )
			dynamic_loader="${base_libdir}/ld-musl-powerpc${@['', '-sf'][d.getVar('TARGET_FPU') == 'soft']}.so.1"
			;;
		powerpc64 )
			dynamic_loader="${base_libdir}/ld-musl-powerpc64.so.1"
			;;
		x86_64 )
			dynamic_loader="${base_libdir}/ld-musl-x86_64.so.1"
			;;
		i*86 )
			dynamic_loader="${base_libdir}/ld-musl-i386.so.1"
			;;
		arm* )
			dynamic_loader="${base_libdir}/ld-musl-arm${ARMPKGSFX_ENDIAN}${ARMPKGSFX_EABI}.so.1"
			;;
		aarch64* )
			dynamic_loader="${base_libdir}/ld-musl-aarch64${ARMPKGSFX_ENDIAN_64}.so.1"
			;;
		* )
			dynamic_loader="/unknown_dynamic_linker"
			;;
	esac
	echo $dynamic_loader
}

linuxloader () {
	linuxloader_${LDSO_TCLIBC}
}
