From: Otavio Salvador <otavio@ossystems.com.br>
Subject: [PATCH] build: Check packages to be used by the enabled flavors
Organization: O.S. Systems Software LTDA.

The packages shouldn't be dynamically detected otherwise the build
predictability is lost. We now have all packages as mandatory but
dependent of the flavors which use them.

Upstream-Status: Submitted [https://github.com/glmark2/glmark2/pull/8]

Signed-off-by: Otavio Salvador <otavio@ossystems.com.br>
---
 wscript | 10 +++++++---
 1 file changed, 7 insertions(+), 3 deletions(-)

diff --git a/wscript b/wscript
index cab62a3..e7eaed0 100644
--- a/wscript
+++ b/wscript
@@ -121,13 +121,17 @@ def configure(ctx):
                 ('mirclient','mirclient', '0.13', list_contains(Options.options.flavors, 'mir')),
                 ('wayland-client','wayland-client', None, list_contains(Options.options.flavors, 'wayland')),
                 ('wayland-egl','wayland-egl', None, list_contains(Options.options.flavors, 'wayland'))]
-    for (pkg, uselib, atleast, mandatory) in opt_pkgs:
+    for (pkg, uselib, atleast, check) in opt_pkgs:
+        # Check packages required by the flavors
+        if not check:
+            continue
+
         if atleast is None:
             ctx.check_cfg(package = pkg, uselib_store = uselib,
-                          args = '--cflags --libs', mandatory = mandatory)
+                          args = '--cflags --libs', mandatory = True)
         else:
             ctx.check_cfg(package = pkg, uselib_store = uselib, atleast_version=atleast,
-                          args = '--cflags --libs', mandatory = mandatory)
+                          args = '--cflags --libs', mandatory = True)
 
 
     # Prepend CXX flags so that they can be overriden by the
-- 
2.4.6

