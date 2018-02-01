# For runqemu
IMAGE_CLASSES += "qemuboot"
QB_SYSTEM_NAME_x86 = "qemu-system-i386"
QB_CPU_x86 = "-cpu pentium2"
QB_CPU_KVM_x86 = "-cpu pentium2"

QB_SYSTEM_NAME_x86-64 = "qemu-system-x86_64"
QB_CPU_x86-64 = "-cpu core2duo"
QB_CPU_KVM_x86-64 = "-cpu core2duo"

QB_AUDIO_DRV = "alsa"
QB_AUDIO_OPT = "-soundhw ac97,es1370"
QB_KERNEL_CMDLINE_APPEND = "vga=0 uvesafb.mode_option=${UVESA_MODE} oprofile.timer=1 uvesafb.task_timeout=-1"
# Add the 'virtio-rng-pci' device otherwise the guest may run out of entropy
QB_OPT_APPEND = "-vga vmware -show-cursor -usb -usbdevice tablet -device virtio-rng-pci"

KERNEL_MODULE_AUTOLOAD += "uvesafb"
KERNEL_MODULE_PROBECONF += "uvesafb"
UVESA_MODE ?= "640x480-32"
module_conf_uvesafb = "options uvesafb mode_option=${UVESA_MODE}"
