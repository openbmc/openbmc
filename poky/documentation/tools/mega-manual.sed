# Processes poky-ref-manual and yocto-project-qs manual (<word>-<word>-<word> style).
# This style is for manual folders like "yocto-project-qs" and "poky-ref-manual".
# This is the old way that did it.  Can't do that now that we have "bitbake-user-manual" strings
# in the mega-manual.
# s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/[a-z]*-[a-z]*-[a-z]*/[a-z]*-[a-z]*-[a-z]*.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/yocto-project-qs/yocto-project-qs.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/poky-ref-manual/poky-ref-manual.html#@"link" href="#@g

# Processes all other manuals (<word>-<word> style) except for the BitBake User Manual because
# it is not included in the mega-manual.
# This style is for manual folders that use two word, which is the standard now (e.g. "ref-manual").
# This was the one-liner that worked before we introduced the BitBake User Manual, which is
# not in the mega-manual.
# s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/[a-z]*-[a-z]*/[a-z]*-[a-z]*.html#@"link" href="#@g

s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/sdk-manual/sdk-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/bsp-guide/bsp-guide.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/dev-manual/dev-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/overview-manual/overview-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/brief-yoctoprojectqs/brief-yoctoprojectqs.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/kernel-dev/kernel-dev.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/profile-manual/profile-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/ref-manual/ref-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/2.5.2/toaster-manual/toaster-manual.html#@"link" href="#@g

# Process cases where just an external manual is referenced without an id anchor
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/brief-yoctoprojectqs/brief-yoctoprojectqs.html" target="_top">Yocto Project Quick Build</a>@Yocto Project Quick Build@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/yocto-project-qs/yocto-project-qs.html" target="_top">Yocto Project Quick Start</a>@Yocto Project Quick Start@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/dev-manual/dev-manual.html" target="_top">Yocto Project Development Tasks Manual</a>@Yocto Project Development Tasks Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/overview-manual/overview-manual.html" target="_top">Yocto Project Overview and Concepts Manual</a>@Yocto project Overview and Concepts Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/sdk-manual/sdk-manual.html" target="_top">Yocto Project Application Development and the Extensible Software Development Kit (eSDK)</a>@Yocto Project Application Development and the Extensible Software Development Kit (eSDK)@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/bsp-guide/bsp-guide.html" target="_top">Yocto Project Board Support Package (BSP) Developer's Guide</a>@Yocto Project Board Support Package (BSP) Developer's Guide@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/profile-manual/profile-manual.html" target="_top">Yocto Project Profiling and Tracing Manual</a>@Yocto Project Profiling and Tracing Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/kernel-dev/kernel-dev.html" target="_top">Yocto Project Linux Kernel Development Manual</a>@Yocto Project Linux Kernel Development Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/ref-manual/ref-manual.html" target="_top">Yocto Project Reference Manual</a>@Yocto Project Reference Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/toaster-manual/toaster-manual.html" target="_top">Toaster User Manual</a>@Toaster User Manual@g

# Process a single, rouge occurrence of a linked reference to the Mega-Manual.
s@<a class="ulink" href="http://www.yoctoproject.org/docs/2.5.2/mega-manual/mega-manual.html" target="_top">Yocto Project Mega-Manual</a>@Yocto Project Mega-Manual@g

