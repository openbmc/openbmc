# Processes poky-ref-manual and yocto-project-qs manual (<word>-<word>-<word> style)
# For example:
#   "ulink" href="http://www.yoctoproject.org/docs/1.3/poky-ref-manual/poky-ref-manual.html#faq"
#   -> "link" href="../poky-ref-manual/faq.html"
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/[^\/]*\/\([a-z]*-[a-z]*-[a-z]*\)\/[a-z]*-[a-z]*-[a-z]*.html#\([^\"]*\)\"/\"link\" href=\"\.\.\/\1\/\2.html\"/g

# Processes all other manuals (<word>-<word> style)
# For example:
#   "ulink" href="http://www.yoctoproject.org/docs/1.3/kernel-manual/kernel-manual.html#faq"
#   -> "link" href="../kernel-manual/faq.html"
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/[^\/]*\/\([a-z]*-[a-z]*\)\/[a-z]*-[a-z]*.html#\([^\"]*\)\"/\"link\" href=\"\.\.\/\1\/\2.html\"/g

# Process cases where just an external manual is referenced without an id anchor
# For example:
#   "ulink" href="http://www.yoctoproject.org/docs/1.3/kernel-manual/kernel-manual.html
#   -> "link" href="../kernel-manual/index.html"
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/[^\/]*\/\([a-z]*-[a-z]*-[a-z]*\)\/[a-z]*-[a-z]*-[a-z]*.html\"/\"link\" href=\"\.\.\/\1\/index.html\"/g
s/\"ulink\" href=\"http:\/\/www.yoctoproject.org\/docs\/[^\/]*\/\([a-z]*-[a-z]*\)\/[a-z]*-[a-z]*.html\"/\"link\" href=\"\.\.\/\1\/index.html\"/g
