# For runqemu
IMAGE_CLASSES += "qemuboot"
QB_SMP = "-smp 4"
QB_CPU:x86 = "-cpu IvyBridge -machine q35"
QB_CPU_KVM:x86 = "-cpu IvyBridge -machine q35"

QB_CPU:x86-64 = "-cpu IvyBridge -machine q35"
QB_CPU_KVM:x86-64 = "-cpu IvyBridge -machine q35"

QB_AUDIO_DRV = "alsa"
QB_AUDIO_OPT = "-device AC97"
QB_KERNEL_CMDLINE_APPEND = "oprofile.timer=1 tsc=reliable no_timer_check rcupdate.rcu_expedited=1"
QB_OPT_APPEND = "-usb -device usb-tablet"

