# For runqemu
IMAGE_CLASSES += "qemuboot"
QB_CPU_x86 = "-cpu core2duo"
QB_CPU_KVM_x86 = "-cpu core2duo"

QB_CPU_x86-64 = "-cpu core2duo"
QB_CPU_KVM_x86-64 = "-cpu core2duo"

QB_AUDIO_DRV = "alsa"
QB_AUDIO_OPT = "-soundhw ac97,es1370"
QB_KERNEL_CMDLINE_APPEND = "oprofile.timer=1"
QB_OPT_APPEND = "-show-cursor -usb -device usb-tablet"
# Add the 'virtio-rng-pci' device otherwise the guest may run out of entropy
QB_OPT_APPEND += "-object rng-random,filename=/dev/urandom,id=rng0 -device virtio-rng-pci,rng=rng0"

