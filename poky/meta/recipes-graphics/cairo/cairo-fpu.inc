
def get_cairo_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU') in [ 'soft' ]:
        return "--disable-some-floating-point"
    return ""

