# When booting gem5-arm64 with Xen we need to set the cpu as Cortex A53 and
# remove support for pointer authentification
GEM5_RUN_EXTRA:append = " \
${@bb.utils.contains('DISTRO_FEATURES_NATIVE', 'xen', \
'--param=system.cpu_cluster[0].cpus[0].isa[0].midr=0x410fd030 \
--param=system.cpu_cluster[0].cpus[0].isa[0].id_aa64isar1_el1=0x0', \
'', d)}"
