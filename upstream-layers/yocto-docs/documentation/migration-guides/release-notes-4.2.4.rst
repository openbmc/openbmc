.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Release notes for Yocto-4.2.4 (Mickledore)
------------------------------------------

Security Fixes in Yocto-4.2.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  bind: Fix :cve_nist:`2023-3341` and :cve_nist:`2023-4236`
-  binutils: Fix :cve_nist:`2023-39128`
-  cups: fix :cve_nist:`2023-4504`
-  curl: Fix :cve_nist:`2023-28320`, :cve_nist:`2023-32001`, :cve_nist:`2023-38039`, :cve_nist:`2023-38545` and :cve_nist:`2023-38546`
-  dmidecode: fix for :cve_nist:`2023-30630`
-  dropbear: fix :cve_nist:`2023-36328`
-  ffmpeg: Ignore :cve_nist:`2023-39018`
-  gcc: Fix :cve_nist:`2023-4039`
-  gdb: Fix :cve_nist:`2023-39128`
-  ghostscript: Fix :cve_nist:`2023-38559` and :cve_nist:`2023-43115`
-  glibc: Fix :cve_nist:`2023-4527` and :cve_nist:`2023-4806`
-  go: Fix :cve_nist:`2023-29409` and :cve_nist:`2023-39533`
-  grub: Fix :cve_nist:`2023-4692` and :cve_nist:`2023-4693`
-  gstreamer: Fix :cve_mitre:`2023-40474`, :cve_mitre:`2023-40475` and :cve_mitre:`2023-40476`
-  inetutils: fix :cve_nist:`2023-40303`
-  librsvg: Fix :cve_nist:`2023-38633`
-  libssh2: Fix :cve_nist:`2020-22218`
-  libwebp: Fix :cve_nist:`2023-4863` and :cve_nist:`2023-5129`
-  libx11: Fix :cve_nist:`2023-43785`, :cve_nist:`2023-43786` and :cve_nist:`2023-43787`
-  libxpm: Fix :cve_nist:`2023-43788` and :cve_nist:`2023-43789`
-  linux-yocto/6.1: Ignore :cve_nist:`2003-1604`, :cve_nist:`2004-0230`, :cve_nist:`2006-3635`, :cve_nist:`2006-5331`, :cve_nist:`2006-6128`, :cve_nist:`2007-4774`, :cve_nist:`2007-6761`, :cve_nist:`2007-6762`, :cve_nist:`2008-7316`, :cve_nist:`2009-2692`, :cve_nist:`2010-0008`, :cve_nist:`2010-3432`, :cve_nist:`2010-4648`, :cve_nist:`2010-5313`, :cve_nist:`2010-5328`, :cve_nist:`2010-5329`, :cve_nist:`2010-5331`, :cve_nist:`2010-5332`, :cve_nist:`2011-4098`, :cve_nist:`2011-4131`, :cve_nist:`2011-4915`, :cve_nist:`2011-5321`, :cve_nist:`2011-5327`, :cve_nist:`2012-0957`, :cve_nist:`2012-2119`, :cve_nist:`2012-2136`, :cve_nist:`2012-2137`, :cve_nist:`2012-2313`, :cve_nist:`2012-2319`, :cve_nist:`2012-2372`, :cve_nist:`2012-2375`, :cve_nist:`2012-2390`, :cve_nist:`2012-2669`, :cve_nist:`2012-2744`, :cve_nist:`2012-2745`, :cve_nist:`2012-3364`, :cve_nist:`2012-3375`, :cve_nist:`2012-3400`, :cve_nist:`2012-3412`, :cve_nist:`2012-3430`, :cve_nist:`2012-3510`, :cve_nist:`2012-3511`, :cve_nist:`2012-3520`, :cve_nist:`2012-3552`, :cve_nist:`2012-4398`, :cve_nist:`2012-4444`, :cve_nist:`2012-4461`, :cve_nist:`2012-4467`, :cve_nist:`2012-4508`, :cve_nist:`2012-4530`, :cve_nist:`2012-4565`, :cve_nist:`2012-5374`, :cve_nist:`2012-5375`, :cve_nist:`2012-5517`, :cve_nist:`2012-6536`, :cve_nist:`2012-6537`, :cve_nist:`2012-6538`, :cve_nist:`2012-6539`, :cve_nist:`2012-6540`, :cve_nist:`2012-6541`, :cve_nist:`2012-6542`, :cve_nist:`2012-6543`, :cve_nist:`2012-6544`, :cve_nist:`2012-6545`, :cve_nist:`2012-6546`, :cve_nist:`2012-6547`, :cve_nist:`2012-6548`, :cve_nist:`2012-6549`, :cve_nist:`2012-6638`, :cve_nist:`2012-6647`, :cve_nist:`2012-6657`, :cve_nist:`2012-6689`, :cve_nist:`2012-6701`, :cve_nist:`2012-6703`, :cve_nist:`2012-6704`, :cve_nist:`2012-6712`, :cve_nist:`2013-0160`, :cve_nist:`2013-0190`, :cve_nist:`2013-0216`, :cve_nist:`2013-0217`, :cve_nist:`2013-0228`, :cve_nist:`2013-0231`, :cve_nist:`2013-0268`, :cve_nist:`2013-0290`, :cve_nist:`2013-0309`, :cve_nist:`2013-0310`, :cve_nist:`2013-0311`, :cve_nist:`2013-0313`, :cve_nist:`2013-0343`, :cve_nist:`2013-0349`, :cve_nist:`2013-0871`, :cve_nist:`2013-0913`, :cve_nist:`2013-0914`, :cve_nist:`2013-1059`, :cve_nist:`2013-1763`, :cve_nist:`2013-1767`, :cve_nist:`2013-1772`, :cve_nist:`2013-1773`, :cve_nist:`2013-1774`, :cve_nist:`2013-1792`, :cve_nist:`2013-1796`, :cve_nist:`2013-1797`, :cve_nist:`2013-1798`, :cve_nist:`2013-1819`, :cve_nist:`2013-1826`, :cve_nist:`2013-1827`, :cve_nist:`2013-1828`, :cve_nist:`2013-1848`, :cve_nist:`2013-1858`, :cve_nist:`2013-1860`, :cve_nist:`2013-1928`, :cve_nist:`2013-1929`, :cve_nist:`2013-1943`, :cve_nist:`2013-1956`, :cve_nist:`2013-1957`, :cve_nist:`2013-1958`, :cve_nist:`2013-1959`, :cve_nist:`2013-1979`, :cve_nist:`2013-2015`, :cve_nist:`2013-2017`, :cve_nist:`2013-2058`, :cve_nist:`2013-2094`, :cve_nist:`2013-2128`, :cve_nist:`2013-2140`, :cve_nist:`2013-2141`, :cve_nist:`2013-2146`, :cve_nist:`2013-2147`, :cve_nist:`2013-2148`, :cve_nist:`2013-2164`, :cve_nist:`2013-2206`, :cve_nist:`2013-2232`, :cve_nist:`2013-2234`, :cve_nist:`2013-2237`, :cve_nist:`2013-2546`, :cve_nist:`2013-2547`, :cve_nist:`2013-2548`, :cve_nist:`2013-2596`, :cve_nist:`2013-2634`, :cve_nist:`2013-2635`, :cve_nist:`2013-2636`, :cve_nist:`2013-2850`, :cve_nist:`2013-2851`, :cve_nist:`2013-2852`, :cve_nist:`2013-2888`, :cve_nist:`2013-2889`, :cve_nist:`2013-2890`, :cve_nist:`2013-2891`, :cve_nist:`2013-2892`, :cve_nist:`2013-2893`, :cve_nist:`2013-2894`, :cve_nist:`2013-2895`, :cve_nist:`2013-2896`, :cve_nist:`2013-2897`, :cve_nist:`2013-2898`, :cve_nist:`2013-2899`, :cve_nist:`2013-2929`, :cve_nist:`2013-2930`, :cve_nist:`2013-3076`, :cve_nist:`2013-3222`, :cve_nist:`2013-3223`, :cve_nist:`2013-3224`, :cve_nist:`2013-3225`, :cve_nist:`2013-3226`, :cve_nist:`2013-3227`, :cve_nist:`2013-3228`, :cve_nist:`2013-3229`, :cve_nist:`2013-3230`, :cve_nist:`2013-3231`, :cve_nist:`2013-3232`, :cve_nist:`2013-3233`, :cve_nist:`2013-3234`, :cve_nist:`2013-3235`, :cve_nist:`2013-3236`, :cve_nist:`2013-3237`, :cve_nist:`2013-3301`, :cve_nist:`2013-3302`, :cve_nist:`2013-4125`, :cve_nist:`2013-4127`, :cve_nist:`2013-4129`, :cve_nist:`2013-4162`, :cve_nist:`2013-4163`, :cve_nist:`2013-4205`, :cve_nist:`2013-4220`, :cve_nist:`2013-4247`, :cve_nist:`2013-4254`, :cve_nist:`2013-4270`, :cve_nist:`2013-4299`, :cve_nist:`2013-4300`, :cve_nist:`2013-4312`, :cve_nist:`2013-4343`, :cve_nist:`2013-4345`, :cve_nist:`2013-4348`, :cve_nist:`2013-4350`, :cve_nist:`2013-4387`, :cve_nist:`2013-4470`, :cve_nist:`2013-4483`, :cve_nist:`2013-4511`, :cve_nist:`2013-4512`, :cve_nist:`2013-4513`, :cve_nist:`2013-4514`, :cve_nist:`2013-4515`, :cve_nist:`2013-4516`, :cve_nist:`2013-4563`, :cve_nist:`2013-4579`, :cve_nist:`2013-4587`, :cve_nist:`2013-4588`, :cve_nist:`2013-4591`, :cve_nist:`2013-4592`, :cve_nist:`2013-5634`, :cve_nist:`2013-6282`, :cve_nist:`2013-6367`, :cve_nist:`2013-6368`, :cve_nist:`2013-6376`, :cve_nist:`2013-6378`, :cve_nist:`2013-6380`, :cve_nist:`2013-6381`, :cve_nist:`2013-6382`, :cve_nist:`2013-6383`, :cve_nist:`2013-6431`, :cve_nist:`2013-6432`, :cve_nist:`2013-6885`, :cve_nist:`2013-7026`, :cve_nist:`2013-7027`, :cve_nist:`2013-7263`, :cve_nist:`2013-7264`, :cve_nist:`2013-7265`, :cve_nist:`2013-7266`, :cve_nist:`2013-7267`, :cve_nist:`2013-7268`, :cve_nist:`2013-7269`, :cve_nist:`2013-7270`, :cve_nist:`2013-7271`, :cve_nist:`2013-7281`, :cve_nist:`2013-7339`, :cve_nist:`2013-7348`, :cve_nist:`2013-7421`, :cve_nist:`2013-7446`, :cve_nist:`2013-7470`, :cve_nist:`2014-0038`, :cve_nist:`2014-0049`, :cve_nist:`2014-0055`, :cve_nist:`2014-0069`, :cve_nist:`2014-0077`, :cve_nist:`2014-0100`, :cve_nist:`2014-0101`, :cve_nist:`2014-0102`, :cve_nist:`2014-0131`, :cve_nist:`2014-0155`, :cve_nist:`2014-0181`, :cve_nist:`2014-0196`, :cve_nist:`2014-0203`, :cve_nist:`2014-0205`, :cve_nist:`2014-0206`, :cve_nist:`2014-1438`, :cve_nist:`2014-1444`, :cve_nist:`2014-1445`, :cve_nist:`2014-1446`, :cve_nist:`2014-1690`, :cve_nist:`2014-1737`, :cve_nist:`2014-1738`, :cve_nist:`2014-1739`, :cve_nist:`2014-1874`, :cve_nist:`2014-2038`, :cve_nist:`2014-2039`, :cve_nist:`2014-2309`, :cve_nist:`2014-2523`, :cve_nist:`2014-2568`, :cve_nist:`2014-2580`, :cve_nist:`2014-2672`, :cve_nist:`2014-2673`, :cve_nist:`2014-2678`, :cve_nist:`2014-2706`, :cve_nist:`2014-2739`, :cve_nist:`2014-2851`, :cve_nist:`2014-2889`, :cve_nist:`2014-3122`, :cve_nist:`2014-3144`, :cve_nist:`2014-3145`, :cve_nist:`2014-3153`, :cve_nist:`2014-3180`, :cve_nist:`2014-3181`, :cve_nist:`2014-3182`, :cve_nist:`2014-3183`, :cve_nist:`2014-3184`, :cve_nist:`2014-3185`, :cve_nist:`2014-3186`, :cve_nist:`2014-3534`, :cve_nist:`2014-3535`, :cve_nist:`2014-3601`, :cve_nist:`2014-3610`, :cve_nist:`2014-3611`, :cve_nist:`2014-3631`, :cve_nist:`2014-3645`, :cve_nist:`2014-3646`, :cve_nist:`2014-3647`, :cve_nist:`2014-3673`, :cve_nist:`2014-3687`, :cve_nist:`2014-3688`, :cve_nist:`2014-3690`, :cve_nist:`2014-3917`, :cve_nist:`2014-3940`, :cve_nist:`2014-4014`, :cve_nist:`2014-4027`, :cve_nist:`2014-4157`, :cve_nist:`2014-4171`, :cve_nist:`2014-4508`, :cve_nist:`2014-4608`, :cve_nist:`2014-4611`, :cve_nist:`2014-4652`, :cve_nist:`2014-4653`, :cve_nist:`2014-4654`, :cve_nist:`2014-4655`, :cve_nist:`2014-4656`, :cve_nist:`2014-4667`, :cve_nist:`2014-4699`, :cve_nist:`2014-4943`, :cve_nist:`2014-5045`, :cve_nist:`2014-5077`, :cve_nist:`2014-5206`, :cve_nist:`2014-5207`, :cve_nist:`2014-5471`, :cve_nist:`2014-5472`, :cve_nist:`2014-6410`, :cve_nist:`2014-6416`, :cve_nist:`2014-6417`, :cve_nist:`2014-6418`, :cve_nist:`2014-7145`, :cve_nist:`2014-7283`, :cve_nist:`2014-7284`, :cve_nist:`2014-7822`, :cve_nist:`2014-7825`, :cve_nist:`2014-7826`, :cve_nist:`2014-7841`, :cve_nist:`2014-7842`, :cve_nist:`2014-7843`, :cve_nist:`2014-7970`, :cve_nist:`2014-7975`, :cve_nist:`2014-8086`, :cve_nist:`2014-8133`, :cve_nist:`2014-8134`, :cve_nist:`2014-8159`, :cve_nist:`2014-8160`, :cve_nist:`2014-8171`, :cve_nist:`2014-8172`, :cve_nist:`2014-8173`, :cve_nist:`2014-8369`, :cve_nist:`2014-8480`, :cve_nist:`2014-8481`, :cve_nist:`2014-8559`, :cve_nist:`2014-8709`, :cve_nist:`2014-8884`, :cve_nist:`2014-8989`, :cve_nist:`2014-9090`, :cve_nist:`2014-9322`, :cve_nist:`2014-9419`, :cve_nist:`2014-9420`, :cve_nist:`2014-9428`, :cve_nist:`2014-9529`, :cve_nist:`2014-9584`, :cve_nist:`2014-9585`, :cve_nist:`2014-9644`, :cve_nist:`2014-9683`, :cve_nist:`2014-9710`, :cve_nist:`2014-9715`, :cve_nist:`2014-9717`, :cve_nist:`2014-9728`, :cve_nist:`2014-9729`, :cve_nist:`2014-9730`, :cve_nist:`2014-9731`, :cve_nist:`2014-9803`, :cve_nist:`2014-9870`, :cve_nist:`2014-9888`, :cve_nist:`2014-9895`, :cve_nist:`2014-9903`, :cve_nist:`2014-9904`, :cve_nist:`2014-9914`, :cve_nist:`2014-9922`, :cve_nist:`2014-9940`, :cve_nist:`2015-0239`, :cve_nist:`2015-0274`, :cve_nist:`2015-0275`, :cve_nist:`2015-1333`, :cve_nist:`2015-1339`, :cve_nist:`2015-1350`, :cve_nist:`2015-1420`, :cve_nist:`2015-1421`, :cve_nist:`2015-1465`, :cve_nist:`2015-1573`, :cve_nist:`2015-1593`, :cve_nist:`2015-1805`, :cve_nist:`2015-2041`, :cve_nist:`2015-2042`, :cve_nist:`2015-2150`, :cve_nist:`2015-2666`, :cve_nist:`2015-2672`, :cve_nist:`2015-2686`, :cve_nist:`2015-2830`, :cve_nist:`2015-2922`, :cve_nist:`2015-2925`, :cve_nist:`2015-3212`, :cve_nist:`2015-3214`, :cve_nist:`2015-3288`, :cve_nist:`2015-3290`, :cve_nist:`2015-3291`, :cve_nist:`2015-3331`, :cve_nist:`2015-3339`, :cve_nist:`2015-3636`, :cve_nist:`2015-4001`, :cve_nist:`2015-4002`, :cve_nist:`2015-4003`, :cve_nist:`2015-4004`, :cve_nist:`2015-4036`, :cve_nist:`2015-4167`, :cve_nist:`2015-4170`, :cve_nist:`2015-4176`, :cve_nist:`2015-4177`, :cve_nist:`2015-4178`, :cve_nist:`2015-4692`, :cve_nist:`2015-4700`, :cve_nist:`2015-5156`, :cve_nist:`2015-5157`, :cve_nist:`2015-5257`, :cve_nist:`2015-5283`, :cve_nist:`2015-5307`, :cve_nist:`2015-5327`, :cve_nist:`2015-5364`, :cve_nist:`2015-5366`, :cve_nist:`2015-5697`, :cve_nist:`2015-5706`, :cve_nist:`2015-5707`, :cve_nist:`2015-6252`, :cve_nist:`2015-6526`, :cve_nist:`2015-6937`, :cve_nist:`2015-7509`, :cve_nist:`2015-7513`, :cve_nist:`2015-7515`, :cve_nist:`2015-7550`, :cve_nist:`2015-7566`, :cve_nist:`2015-7613`, :cve_nist:`2015-7799`, :cve_nist:`2015-7833`, :cve_nist:`2015-7872`, :cve_nist:`2015-7884`, :cve_nist:`2015-7885`, :cve_nist:`2015-7990`, :cve_nist:`2015-8104`, :cve_nist:`2015-8215`, :cve_nist:`2015-8324`, :cve_nist:`2015-8374`, :cve_nist:`2015-8539`, :cve_nist:`2015-8543`, :cve_nist:`2015-8550`, :cve_nist:`2015-8551`, :cve_nist:`2015-8552`, :cve_nist:`2015-8553`, :cve_nist:`2015-8569`, :cve_nist:`2015-8575`, :cve_nist:`2015-8660`, :cve_nist:`2015-8709`, :cve_nist:`2015-8746`, :cve_nist:`2015-8767`, :cve_nist:`2015-8785`, :cve_nist:`2015-8787`, :cve_nist:`2015-8812`, :cve_nist:`2015-8816`, :cve_nist:`2015-8830`, :cve_nist:`2015-8839`, :cve_nist:`2015-8844`, :cve_nist:`2015-8845`, :cve_nist:`2015-8950`, :cve_nist:`2015-8952`, :cve_nist:`2015-8953`, :cve_nist:`2015-8955`, :cve_nist:`2015-8956`, :cve_nist:`2015-8961`, :cve_nist:`2015-8962`, :cve_nist:`2015-8963`, :cve_nist:`2015-8964`, :cve_nist:`2015-8966`, :cve_nist:`2015-8967`, :cve_nist:`2015-8970`, :cve_nist:`2015-9004`, :cve_nist:`2015-9016`, :cve_nist:`2015-9289`, :cve_nist:`2016-0617`, :cve_nist:`2016-0723`, :cve_nist:`2016-0728`, :cve_nist:`2016-0758`, :cve_nist:`2016-0821`, :cve_nist:`2016-0823`, :cve_nist:`2016-10044`, :cve_nist:`2016-10088`, :cve_nist:`2016-10147`, :cve_nist:`2016-10150`, :cve_nist:`2016-10153`, :cve_nist:`2016-10154`, :cve_nist:`2016-10200`, :cve_nist:`2016-10208`, :cve_nist:`2016-10229`, :cve_nist:`2016-10318`, :cve_nist:`2016-10723`, :cve_nist:`2016-10741`, :cve_nist:`2016-10764`, :cve_nist:`2016-10905`, :cve_nist:`2016-10906`, :cve_nist:`2016-10907`, :cve_nist:`2016-1237`, :cve_nist:`2016-1575`, :cve_nist:`2016-1576`, :cve_nist:`2016-1583`, :cve_nist:`2016-2053`, :cve_nist:`2016-2069`, :cve_nist:`2016-2070`, :cve_nist:`2016-2085`, :cve_nist:`2016-2117`, :cve_nist:`2016-2143`, :cve_nist:`2016-2184`, :cve_nist:`2016-2185`, :cve_nist:`2016-2186`, :cve_nist:`2016-2187`, :cve_nist:`2016-2188`, :cve_nist:`2016-2383`, :cve_nist:`2016-2384`, :cve_nist:`2016-2543`, :cve_nist:`2016-2544`, :cve_nist:`2016-2545`, :cve_nist:`2016-2546`, :cve_nist:`2016-2547`, :cve_nist:`2016-2548`, :cve_nist:`2016-2549`, :cve_nist:`2016-2550`, :cve_nist:`2016-2782`, :cve_nist:`2016-2847`, :cve_nist:`2016-3044`, :cve_nist:`2016-3070`, :cve_nist:`2016-3134`, :cve_nist:`2016-3135`, :cve_nist:`2016-3136`, :cve_nist:`2016-3137`, :cve_nist:`2016-3138`, :cve_nist:`2016-3139`, :cve_nist:`2016-3140`, :cve_nist:`2016-3156`, :cve_nist:`2016-3157`, :cve_nist:`2016-3672`, :cve_nist:`2016-3689`, :cve_nist:`2016-3713`, :cve_nist:`2016-3841`, :cve_nist:`2016-3857`, :cve_nist:`2016-3951`, :cve_nist:`2016-3955`, :cve_nist:`2016-3961`, :cve_nist:`2016-4440`, :cve_nist:`2016-4470`, :cve_nist:`2016-4482`, :cve_nist:`2016-4485`, :cve_nist:`2016-4486`, :cve_nist:`2016-4557`, :cve_nist:`2016-4558`, :cve_nist:`2016-4565`, :cve_nist:`2016-4568`, :cve_nist:`2016-4569`, :cve_nist:`2016-4578`, :cve_nist:`2016-4580`, :cve_nist:`2016-4581`, :cve_nist:`2016-4794`, :cve_nist:`2016-4805`, :cve_nist:`2016-4913`, :cve_nist:`2016-4951`, :cve_nist:`2016-4997`, :cve_nist:`2016-4998`, :cve_nist:`2016-5195`, :cve_nist:`2016-5243`, :cve_nist:`2016-5244`, :cve_nist:`2016-5400`, :cve_nist:`2016-5412`, :cve_nist:`2016-5696`, :cve_nist:`2016-5728`, :cve_nist:`2016-5828`, :cve_nist:`2016-5829`, :cve_nist:`2016-6130`, :cve_nist:`2016-6136`, :cve_nist:`2016-6156`, :cve_nist:`2016-6162`, :cve_nist:`2016-6187`, :cve_nist:`2016-6197`, :cve_nist:`2016-6198`, :cve_nist:`2016-6213`, :cve_nist:`2016-6327`, :cve_nist:`2016-6480`, :cve_nist:`2016-6516`, :cve_nist:`2016-6786`, :cve_nist:`2016-6787`, :cve_nist:`2016-6828`, :cve_nist:`2016-7039`, :cve_nist:`2016-7042`, :cve_nist:`2016-7097`, :cve_nist:`2016-7117`, :cve_nist:`2016-7425`, :cve_nist:`2016-7910`, :cve_nist:`2016-7911`, :cve_nist:`2016-7912`, :cve_nist:`2016-7913`, :cve_nist:`2016-7914`, :cve_nist:`2016-7915`, :cve_nist:`2016-7916`, :cve_nist:`2016-7917`, :cve_nist:`2016-8399`, :cve_nist:`2016-8405`, :cve_nist:`2016-8630`, :cve_nist:`2016-8632`, :cve_nist:`2016-8633`, :cve_nist:`2016-8636`, :cve_nist:`2016-8645`, :cve_nist:`2016-8646`, :cve_nist:`2016-8650`, :cve_nist:`2016-8655`, :cve_nist:`2016-8658`, :cve_nist:`2016-8666`, :cve_nist:`2016-9083`, :cve_nist:`2016-9084`, :cve_nist:`2016-9120`, :cve_nist:`2016-9178`, :cve_nist:`2016-9191`, :cve_nist:`2016-9313`, :cve_nist:`2016-9555`, :cve_nist:`2016-9576`, :cve_nist:`2016-9588`, :cve_nist:`2016-9604`, :cve_nist:`2016-9685`, :cve_nist:`2016-9754`, :cve_nist:`2016-9755`, :cve_nist:`2016-9756`, :cve_nist:`2016-9777`, :cve_nist:`2016-9793`, :cve_nist:`2016-9794`, :cve_nist:`2016-9806`, :cve_nist:`2016-9919`, :cve_nist:`2017-0605`, :cve_nist:`2017-0627`, :cve_nist:`2017-0750`, :cve_nist:`2017-0786`, :cve_nist:`2017-0861`, :cve_nist:`2017-1000`, :cve_nist:`2017-1000111`, :cve_nist:`2017-1000112`, :cve_nist:`2017-1000251`, :cve_nist:`2017-1000252`, :cve_nist:`2017-1000253`, :cve_nist:`2017-1000255`, :cve_nist:`2017-1000363`, :cve_nist:`2017-1000364`, :cve_nist:`2017-1000365`, :cve_nist:`2017-1000370`, :cve_nist:`2017-1000371`, :cve_nist:`2017-1000379`, :cve_nist:`2017-1000380`, :cve_nist:`2017-1000405`, :cve_nist:`2017-1000407`, :cve_nist:`2017-1000410`, :cve_nist:`2017-10661`, :cve_nist:`2017-10662`, :cve_nist:`2017-10663`, :cve_nist:`2017-10810`, :cve_nist:`2017-10911`, :cve_nist:`2017-11089`, :cve_nist:`2017-11176`, :cve_nist:`2017-11472`, :cve_nist:`2017-11473`, :cve_nist:`2017-11600`, :cve_nist:`2017-12134`, :cve_nist:`2017-12146`, :cve_nist:`2017-12153`, :cve_nist:`2017-12154`, :cve_nist:`2017-12168`, :cve_nist:`2017-12188`, :cve_nist:`2017-12190`, :cve_nist:`2017-12192`, :cve_nist:`2017-12193`, :cve_nist:`2017-12762`, :cve_nist:`2017-13080`, :cve_nist:`2017-13166`, :cve_nist:`2017-13167`, :cve_nist:`2017-13168`, :cve_nist:`2017-13215`, :cve_nist:`2017-13216`, :cve_nist:`2017-13220`, :cve_nist:`2017-13305`, :cve_nist:`2017-13686`, :cve_nist:`2017-13695`, :cve_nist:`2017-13715`, :cve_nist:`2017-14051`, :cve_nist:`2017-14106`, :cve_nist:`2017-14140`, :cve_nist:`2017-14156`, :cve_nist:`2017-14340`, :cve_nist:`2017-14489`, :cve_nist:`2017-14497`, :cve_nist:`2017-14954`, :cve_nist:`2017-14991`, :cve_nist:`2017-15102`, :cve_nist:`2017-15115`, :cve_nist:`2017-15116`, :cve_nist:`2017-15121`, :cve_nist:`2017-15126`, :cve_nist:`2017-15127`, :cve_nist:`2017-15128`, :cve_nist:`2017-15129`, :cve_nist:`2017-15265`, :cve_nist:`2017-15274`, :cve_nist:`2017-15299`, :cve_nist:`2017-15306`, :cve_nist:`2017-15537`, :cve_nist:`2017-15649`, :cve_nist:`2017-15868`, :cve_nist:`2017-15951`, :cve_nist:`2017-16525`, :cve_nist:`2017-16526`, :cve_nist:`2017-16527`, :cve_nist:`2017-16528`, :cve_nist:`2017-16529`, :cve_nist:`2017-16530`, :cve_nist:`2017-16531`, :cve_nist:`2017-16532`, :cve_nist:`2017-16533`, :cve_nist:`2017-16534`, :cve_nist:`2017-16535`, :cve_nist:`2017-16536`, :cve_nist:`2017-16537`, :cve_nist:`2017-16538`, :cve_nist:`2017-16643`, :cve_nist:`2017-16644`, :cve_nist:`2017-16645`, :cve_nist:`2017-16646`, :cve_nist:`2017-16647`, :cve_nist:`2017-16648`, :cve_nist:`2017-16649`, :cve_nist:`2017-16650`, :cve_nist:`2017-16911`, :cve_nist:`2017-16912`, :cve_nist:`2017-16913`, :cve_nist:`2017-16914`, :cve_nist:`2017-16939`, :cve_nist:`2017-16994`, :cve_nist:`2017-16995`, :cve_nist:`2017-16996`, :cve_nist:`2017-17052`, :cve_nist:`2017-17053`, :cve_nist:`2017-17448`, :cve_nist:`2017-17449`, :cve_nist:`2017-17450`, :cve_nist:`2017-17558`, :cve_nist:`2017-17712`, :cve_nist:`2017-17741`, :cve_nist:`2017-17805`, :cve_nist:`2017-17806`, :cve_nist:`2017-17807`, :cve_nist:`2017-17852`, :cve_nist:`2017-17853`, :cve_nist:`2017-17854`, :cve_nist:`2017-17855`, :cve_nist:`2017-17856`, :cve_nist:`2017-17857`, :cve_nist:`2017-17862`, :cve_nist:`2017-17863`, :cve_nist:`2017-17864`, :cve_nist:`2017-17975`, :cve_nist:`2017-18017`, :cve_nist:`2017-18075`, :cve_nist:`2017-18079`, :cve_nist:`2017-18174`, :cve_nist:`2017-18193`, :cve_nist:`2017-18200`, :cve_nist:`2017-18202`, :cve_nist:`2017-18203`, :cve_nist:`2017-18204`, :cve_nist:`2017-18208`, :cve_nist:`2017-18216`, :cve_nist:`2017-18218`, :cve_nist:`2017-18221`, :cve_nist:`2017-18222`, :cve_nist:`2017-18224`, :cve_nist:`2017-18232`, :cve_nist:`2017-18241`, :cve_nist:`2017-18249`, :cve_nist:`2017-18255`, :cve_nist:`2017-18257`, :cve_nist:`2017-18261`, :cve_nist:`2017-18270`, :cve_nist:`2017-18344`, :cve_nist:`2017-18360`, :cve_nist:`2017-18379`, :cve_nist:`2017-18509`, :cve_nist:`2017-18549`, :cve_nist:`2017-18550`, :cve_nist:`2017-18551`, :cve_nist:`2017-18552`, :cve_nist:`2017-18595`, :cve_nist:`2017-2583`, :cve_nist:`2017-2584`, :cve_nist:`2017-2596`, :cve_nist:`2017-2618`, :cve_nist:`2017-2634`, :cve_nist:`2017-2636`, :cve_nist:`2017-2647`, :cve_nist:`2017-2671`, :cve_nist:`2017-5123`, :cve_nist:`2017-5546`, :cve_nist:`2017-5547`, :cve_nist:`2017-5548`, :cve_nist:`2017-5549`, :cve_nist:`2017-5550`, :cve_nist:`2017-5551`, :cve_nist:`2017-5576`, :cve_nist:`2017-5577`, :cve_nist:`2017-5669`, :cve_nist:`2017-5715`, :cve_nist:`2017-5753`, :cve_nist:`2017-5754`, :cve_nist:`2017-5897`, :cve_nist:`2017-5967`, :cve_nist:`2017-5970`, :cve_nist:`2017-5972`, :cve_nist:`2017-5986`, :cve_nist:`2017-6001`, :cve_nist:`2017-6074`, :cve_nist:`2017-6214`, :cve_nist:`2017-6345`, :cve_nist:`2017-6346`, :cve_nist:`2017-6347`, :cve_nist:`2017-6348`, :cve_nist:`2017-6353`, :cve_nist:`2017-6874`, :cve_nist:`2017-6951`, :cve_nist:`2017-7184`, :cve_nist:`2017-7187`, :cve_nist:`2017-7261`, :cve_nist:`2017-7273`, :cve_nist:`2017-7277`, :cve_nist:`2017-7294`, :cve_nist:`2017-7308`, :cve_nist:`2017-7346`, :cve_nist:`2017-7374`, :cve_nist:`2017-7472`, :cve_nist:`2017-7477`, :cve_nist:`2017-7482`, :cve_nist:`2017-7487`, :cve_nist:`2017-7495`, :cve_nist:`2017-7518`, :cve_nist:`2017-7533`, :cve_nist:`2017-7541`, :cve_nist:`2017-7542`, :cve_nist:`2017-7558`, :cve_nist:`2017-7616`, :cve_nist:`2017-7618`, :cve_nist:`2017-7645`, :cve_nist:`2017-7889`, :cve_nist:`2017-7895`, :cve_nist:`2017-7979`, :cve_nist:`2017-8061`, :cve_nist:`2017-8062`, :cve_nist:`2017-8063`, :cve_nist:`2017-8064`, :cve_nist:`2017-8065`, :cve_nist:`2017-8066`, :cve_nist:`2017-8067`, :cve_nist:`2017-8068`, :cve_nist:`2017-8069`, :cve_nist:`2017-8070`, :cve_nist:`2017-8071`, :cve_nist:`2017-8072`, :cve_nist:`2017-8106`, :cve_nist:`2017-8240`, :cve_nist:`2017-8797`, :cve_nist:`2017-8824`, :cve_nist:`2017-8831`, :cve_nist:`2017-8890`, :cve_nist:`2017-8924`, :cve_nist:`2017-8925`, :cve_nist:`2017-9059`, :cve_nist:`2017-9074`, :cve_nist:`2017-9075`, :cve_nist:`2017-9076`, :cve_nist:`2017-9077`, :cve_nist:`2017-9150`, :cve_nist:`2017-9211`, :cve_nist:`2017-9242`, :cve_nist:`2017-9605`, :cve_nist:`2017-9725`, :cve_nist:`2017-9984`, :cve_nist:`2017-9985`, :cve_nist:`2017-9986`, :cve_nist:`2018-1000004`, :cve_nist:`2018-1000026`, :cve_nist:`2018-1000028`, :cve_nist:`2018-1000199`, :cve_nist:`2018-1000200`, :cve_nist:`2018-1000204`, :cve_nist:`2018-10021`, :cve_nist:`2018-10074`, :cve_nist:`2018-10087`, :cve_nist:`2018-10124`, :cve_nist:`2018-10322`, :cve_nist:`2018-10323`, :cve_nist:`2018-1065`, :cve_nist:`2018-1066`, :cve_nist:`2018-10675`, :cve_nist:`2018-1068`, :cve_nist:`2018-10840`, :cve_nist:`2018-10853`, :cve_nist:`2018-1087`, :cve_nist:`2018-10876`, :cve_nist:`2018-10877`, :cve_nist:`2018-10878`, :cve_nist:`2018-10879`, :cve_nist:`2018-10880`, :cve_nist:`2018-10881`, :cve_nist:`2018-10882`, :cve_nist:`2018-10883`, :cve_nist:`2018-10901`, :cve_nist:`2018-10902`, :cve_nist:`2018-1091`, :cve_nist:`2018-1092`, :cve_nist:`2018-1093`, :cve_nist:`2018-10938`, :cve_nist:`2018-1094`, :cve_nist:`2018-10940`, :cve_nist:`2018-1095`, :cve_nist:`2018-1108`, :cve_nist:`2018-1118`, :cve_nist:`2018-1120`, :cve_nist:`2018-11232`, :cve_nist:`2018-1128`, :cve_nist:`2018-1129`, :cve_nist:`2018-1130`, :cve_nist:`2018-11412`, :cve_nist:`2018-11506`, :cve_nist:`2018-11508`, :cve_nist:`2018-12126`, :cve_nist:`2018-12127`, :cve_nist:`2018-12130`, :cve_nist:`2018-12207`, :cve_nist:`2018-12232`, :cve_nist:`2018-12233`, :cve_nist:`2018-12633`, :cve_nist:`2018-12714`, :cve_nist:`2018-12896`, :cve_nist:`2018-12904`, :cve_nist:`2018-13053`, :cve_nist:`2018-13093`, :cve_nist:`2018-13094`, :cve_nist:`2018-13095`, :cve_nist:`2018-13096`, :cve_nist:`2018-13097`, :cve_nist:`2018-13098`, :cve_nist:`2018-13099`, :cve_nist:`2018-13100`, :cve_nist:`2018-13405`, :cve_nist:`2018-13406`, :cve_nist:`2018-14609`, :cve_nist:`2018-14610`, :cve_nist:`2018-14611`, :cve_nist:`2018-14612`, :cve_nist:`2018-14613`, :cve_nist:`2018-14614`, :cve_nist:`2018-14615`, :cve_nist:`2018-14616`, :cve_nist:`2018-14617`, :cve_nist:`2018-14619`, :cve_nist:`2018-14625`, :cve_nist:`2018-14633`, :cve_nist:`2018-14634`, :cve_nist:`2018-14641`, :cve_nist:`2018-14646`, :cve_nist:`2018-14656`, :cve_nist:`2018-14678`, :cve_nist:`2018-14734`, :cve_nist:`2018-15471`, :cve_nist:`2018-15572`, :cve_nist:`2018-15594`, :cve_nist:`2018-16276`, :cve_nist:`2018-16597`, :cve_nist:`2018-16658`, :cve_nist:`2018-16862`, :cve_nist:`2018-16871`, :cve_nist:`2018-16880`, :cve_nist:`2018-16882`, :cve_nist:`2018-16884`, :cve_nist:`2018-17182`, :cve_nist:`2018-17972`, :cve_nist:`2018-18021`, :cve_nist:`2018-18281`, :cve_nist:`2018-18386`, :cve_nist:`2018-18397`, :cve_nist:`2018-18445`, :cve_nist:`2018-18559`, :cve_nist:`2018-18690`, :cve_nist:`2018-18710`, :cve_nist:`2018-18955`, :cve_nist:`2018-19406`, :cve_nist:`2018-19407`, :cve_nist:`2018-19824`, :cve_nist:`2018-19854`, :cve_nist:`2018-19985`, :cve_nist:`2018-20169`, :cve_nist:`2018-20449`, :cve_nist:`2018-20509`, :cve_nist:`2018-20510`, :cve_nist:`2018-20511`, :cve_nist:`2018-20669`, :cve_nist:`2018-20784`, :cve_nist:`2018-20836`, :cve_nist:`2018-20854`, :cve_nist:`2018-20855`, :cve_nist:`2018-20856`, :cve_nist:`2018-20961`, :cve_nist:`2018-20976`, :cve_nist:`2018-21008`, :cve_nist:`2018-25015`, :cve_nist:`2018-25020`, :cve_nist:`2018-3620`, :cve_nist:`2018-3639`, :cve_nist:`2018-3646`, :cve_nist:`2018-3665`, :cve_nist:`2018-3693`, :cve_nist:`2018-5332`, :cve_nist:`2018-5333`, :cve_nist:`2018-5344`, :cve_nist:`2018-5390`, :cve_nist:`2018-5391`, :cve_nist:`2018-5703`, :cve_nist:`2018-5750`, :cve_nist:`2018-5803`, :cve_nist:`2018-5814`, :cve_nist:`2018-5848`, :cve_nist:`2018-5873`, :cve_nist:`2018-5953`, :cve_nist:`2018-5995`, :cve_nist:`2018-6412`, :cve_nist:`2018-6554`, :cve_nist:`2018-6555`, :cve_nist:`2018-6927`, :cve_nist:`2018-7191`, :cve_nist:`2018-7273`, :cve_nist:`2018-7480`, :cve_nist:`2018-7492`, :cve_nist:`2018-7566`, :cve_nist:`2018-7740`, :cve_nist:`2018-7754`, :cve_nist:`2018-7755`, :cve_nist:`2018-7757`, :cve_nist:`2018-7995`, :cve_nist:`2018-8043`, :cve_mitre:`2018-8087`, :cve_mitre:`2018-8781`, :cve_mitre:`2018-8822`, :cve_mitre:`2018-8897`, :cve_mitre:`2018-9363`, :cve_mitre:`2018-9385`, :cve_mitre:`2018-9415`, :cve_mitre:`2018-9422`, :cve_mitre:`2018-9465`, :cve_mitre:`2018-9516`, :cve_mitre:`2018-9517`, :cve_mitre:`2018-9518` and :cve_mitre:`2018-9568`
-  linux-yocto/6.1 (Continued): Ignore :cve_nist:`2019-0136`, :cve_nist:`2019-0145`, :cve_nist:`2019-0146`, :cve_nist:`2019-0147`, :cve_nist:`2019-0148`, :cve_nist:`2019-0149`, :cve_nist:`2019-0154`, :cve_nist:`2019-0155`, :cve_nist:`2019-10124`, :cve_nist:`2019-10125`, :cve_nist:`2019-10126`, :cve_nist:`2019-10142`, :cve_nist:`2019-10207`, :cve_nist:`2019-10220`, :cve_nist:`2019-10638`, :cve_nist:`2019-10639`, :cve_nist:`2019-11085`, :cve_nist:`2019-11091`, :cve_nist:`2019-11135`, :cve_nist:`2019-11190`, :cve_nist:`2019-11191`, :cve_nist:`2019-1125`, :cve_nist:`2019-11477`, :cve_nist:`2019-11478`, :cve_nist:`2019-11479`, :cve_nist:`2019-11486`, :cve_nist:`2019-11487`, :cve_nist:`2019-11599`, :cve_nist:`2019-11683`, :cve_nist:`2019-11810`, :cve_nist:`2019-11811`, :cve_nist:`2019-11815`, :cve_nist:`2019-11833`, :cve_nist:`2019-11884`, :cve_nist:`2019-12378`, :cve_nist:`2019-12379`, :cve_nist:`2019-12380`, :cve_nist:`2019-12381`, :cve_nist:`2019-12382`, :cve_nist:`2019-12454`, :cve_nist:`2019-12455`, :cve_nist:`2019-12614`, :cve_nist:`2019-12615`, :cve_nist:`2019-12817`, :cve_nist:`2019-12818`, :cve_nist:`2019-12819`, :cve_nist:`2019-12881`, :cve_nist:`2019-12984`, :cve_nist:`2019-13233`, :cve_nist:`2019-13272`, :cve_nist:`2019-13631`, :cve_nist:`2019-13648`, :cve_nist:`2019-14283`, :cve_nist:`2019-14284`, :cve_nist:`2019-14615`, :cve_nist:`2019-14763`, :cve_nist:`2019-14814`, :cve_nist:`2019-14815`, :cve_nist:`2019-14816`, :cve_nist:`2019-14821`, :cve_nist:`2019-14835`, :cve_nist:`2019-14895`, :cve_nist:`2019-14896`, :cve_nist:`2019-14897`, :cve_nist:`2019-14901`, :cve_nist:`2019-15030`, :cve_nist:`2019-15031`, :cve_nist:`2019-15090`, :cve_nist:`2019-15098`, :cve_nist:`2019-15099`, :cve_nist:`2019-15117`, :cve_nist:`2019-15118`, :cve_nist:`2019-15211`, :cve_nist:`2019-15212`, :cve_nist:`2019-15213`, :cve_nist:`2019-15214`, :cve_nist:`2019-15215`, :cve_nist:`2019-15216`, :cve_nist:`2019-15217`, :cve_nist:`2019-15218`, :cve_nist:`2019-15219`, :cve_nist:`2019-15220`, :cve_nist:`2019-15221`, :cve_nist:`2019-15222`, :cve_nist:`2019-15223`, :cve_nist:`2019-15291`, :cve_nist:`2019-15292`, :cve_nist:`2019-15504`, :cve_nist:`2019-15505`, :cve_nist:`2019-15538`, :cve_nist:`2019-15666`, :cve_nist:`2019-15794`, :cve_nist:`2019-15807`, :cve_nist:`2019-15916`, :cve_nist:`2019-15917`, :cve_nist:`2019-15918`, :cve_nist:`2019-15919`, :cve_nist:`2019-15920`, :cve_nist:`2019-15921`, :cve_nist:`2019-15922`, :cve_nist:`2019-15923`, :cve_nist:`2019-15924`, :cve_nist:`2019-15925`, :cve_nist:`2019-15926`, :cve_nist:`2019-15927`, :cve_nist:`2019-16229`, :cve_nist:`2019-16230`, :cve_nist:`2019-16231`, :cve_nist:`2019-16232`, :cve_nist:`2019-16233`, :cve_nist:`2019-16234`, :cve_nist:`2019-16413`, :cve_nist:`2019-16714`, :cve_nist:`2019-16746`, :cve_nist:`2019-16921`, :cve_nist:`2019-16994`, :cve_nist:`2019-16995`, :cve_nist:`2019-17052`, :cve_nist:`2019-17053`, :cve_nist:`2019-17054`, :cve_nist:`2019-17055`, :cve_nist:`2019-17056`, :cve_nist:`2019-17075`, :cve_nist:`2019-17133`, :cve_nist:`2019-17351`, :cve_nist:`2019-17666`, :cve_nist:`2019-18198`, :cve_nist:`2019-18282`, :cve_nist:`2019-18660`, :cve_nist:`2019-18675`, :cve_nist:`2019-18683`, :cve_nist:`2019-18786`, :cve_nist:`2019-18805`, :cve_nist:`2019-18806`, :cve_nist:`2019-18807`, :cve_nist:`2019-18808`, :cve_nist:`2019-18809`, :cve_nist:`2019-18810`, :cve_nist:`2019-18811`, :cve_nist:`2019-18812`, :cve_nist:`2019-18813`, :cve_nist:`2019-18814`, :cve_nist:`2019-18885`, :cve_nist:`2019-19036`, :cve_nist:`2019-19037`, :cve_nist:`2019-19039`, :cve_nist:`2019-19043`, :cve_nist:`2019-19044`, :cve_nist:`2019-19045`, :cve_nist:`2019-19046`, :cve_nist:`2019-19047`, :cve_nist:`2019-19048`, :cve_nist:`2019-19049`, :cve_nist:`2019-19050`, :cve_nist:`2019-19051`, :cve_nist:`2019-19052`, :cve_nist:`2019-19053`, :cve_nist:`2019-19054`, :cve_nist:`2019-19055`, :cve_nist:`2019-19056`, :cve_nist:`2019-19057`, :cve_nist:`2019-19058`, :cve_nist:`2019-19059`, :cve_nist:`2019-19060`, :cve_nist:`2019-19061`, :cve_nist:`2019-19062`, :cve_nist:`2019-19063`, :cve_nist:`2019-19064`, :cve_nist:`2019-19065`, :cve_nist:`2019-19066`, :cve_nist:`2019-19067`, :cve_nist:`2019-19068`, :cve_nist:`2019-19069`, :cve_nist:`2019-19070`, :cve_nist:`2019-19071`, :cve_nist:`2019-19072`, :cve_nist:`2019-19073`, :cve_nist:`2019-19074`, :cve_nist:`2019-19075`, :cve_nist:`2019-19076`, :cve_nist:`2019-19077`, :cve_nist:`2019-19078`, :cve_nist:`2019-19079`, :cve_nist:`2019-19080`, :cve_nist:`2019-19081`, :cve_nist:`2019-19082`, :cve_nist:`2019-19083`, :cve_nist:`2019-19227`, :cve_nist:`2019-19241`, :cve_nist:`2019-19252`, :cve_nist:`2019-19318`, :cve_nist:`2019-19319`, :cve_nist:`2019-19332`, :cve_nist:`2019-19338`, :cve_nist:`2019-19377`, :cve_nist:`2019-19447`, :cve_nist:`2019-19448`, :cve_nist:`2019-19449`, :cve_nist:`2019-19462`, :cve_nist:`2019-19523`, :cve_nist:`2019-19524`, :cve_nist:`2019-19525`, :cve_nist:`2019-19526`, :cve_nist:`2019-19527`, :cve_nist:`2019-19528`, :cve_nist:`2019-19529`, :cve_nist:`2019-19530`, :cve_nist:`2019-19531`, :cve_nist:`2019-19532`, :cve_nist:`2019-19533`, :cve_nist:`2019-19534`, :cve_nist:`2019-19535`, :cve_nist:`2019-19536`, :cve_nist:`2019-19537`, :cve_nist:`2019-19543`, :cve_nist:`2019-19602`, :cve_nist:`2019-19767`, :cve_nist:`2019-19768`, :cve_nist:`2019-19769`, :cve_nist:`2019-19770`, :cve_nist:`2019-19807`, :cve_nist:`2019-19813`, :cve_nist:`2019-19815`, :cve_nist:`2019-19816`, :cve_nist:`2019-19922`, :cve_nist:`2019-19927`, :cve_nist:`2019-19947`, :cve_nist:`2019-19965`, :cve_nist:`2019-19966`, :cve_nist:`2019-1999`, :cve_nist:`2019-20054`, :cve_nist:`2019-20095`, :cve_nist:`2019-20096`, :cve_nist:`2019-2024`, :cve_nist:`2019-2025`, :cve_nist:`2019-20422`, :cve_nist:`2019-2054`, :cve_nist:`2019-20636`, :cve_nist:`2019-20806`, :cve_nist:`2019-20810`, :cve_nist:`2019-20811`, :cve_nist:`2019-20812`, :cve_nist:`2019-20908`, :cve_nist:`2019-20934`, :cve_nist:`2019-2101`, :cve_nist:`2019-2181`, :cve_nist:`2019-2182`, :cve_nist:`2019-2213`, :cve_nist:`2019-2214`, :cve_nist:`2019-2215`, :cve_nist:`2019-25044`, :cve_nist:`2019-25045`, :cve_nist:`2019-3016`, :cve_nist:`2019-3459`, :cve_nist:`2019-3460`, :cve_nist:`2019-3701`, :cve_nist:`2019-3819`, :cve_nist:`2019-3837`, :cve_nist:`2019-3846`, :cve_nist:`2019-3874`, :cve_nist:`2019-3882`, :cve_nist:`2019-3887`, :cve_nist:`2019-3892`, :cve_nist:`2019-3896`, :cve_nist:`2019-3900`, :cve_nist:`2019-3901`, :cve_nist:`2019-5108`, :cve_nist:`2019-6133`, :cve_nist:`2019-6974`, :cve_nist:`2019-7221`, :cve_nist:`2019-7222`, :cve_nist:`2019-7308`, :cve_nist:`2019-8912`, :cve_nist:`2019-8956`, :cve_nist:`2019-8980`, :cve_nist:`2019-9003`, :cve_nist:`2019-9162`, :cve_nist:`2019-9213`, :cve_nist:`2019-9245`, :cve_nist:`2019-9444`, :cve_nist:`2019-9445`, :cve_nist:`2019-9453`, :cve_nist:`2019-9454`, :cve_nist:`2019-9455`, :cve_nist:`2019-9456`, :cve_nist:`2019-9457`, :cve_nist:`2019-9458`, :cve_nist:`2019-9466`, :cve_nist:`2019-9500`, :cve_nist:`2019-9503`, :cve_nist:`2019-9506`, :cve_nist:`2019-9857`, :cve_nist:`2020-0009`, :cve_nist:`2020-0030`, :cve_nist:`2020-0041`, :cve_nist:`2020-0066`, :cve_nist:`2020-0067`, :cve_nist:`2020-0110`, :cve_nist:`2020-0255`, :cve_nist:`2020-0305`, :cve_nist:`2020-0404`, :cve_nist:`2020-0423`, :cve_nist:`2020-0427`, :cve_nist:`2020-0429`, :cve_nist:`2020-0430`, :cve_nist:`2020-0431`, :cve_nist:`2020-0432`, :cve_nist:`2020-0433`, :cve_nist:`2020-0435`, :cve_nist:`2020-0444`, :cve_nist:`2020-0465`, :cve_nist:`2020-0466`, :cve_nist:`2020-0543`, :cve_nist:`2020-10135`, :cve_nist:`2020-10690`, :cve_nist:`2020-10711`, :cve_nist:`2020-10720`, :cve_nist:`2020-10732`, :cve_nist:`2020-10742`, :cve_nist:`2020-10751`, :cve_nist:`2020-10757`, :cve_nist:`2020-10766`, :cve_nist:`2020-10767`, :cve_nist:`2020-10768`, :cve_nist:`2020-10769`, :cve_nist:`2020-10773`, :cve_nist:`2020-10781`, :cve_nist:`2020-10942`, :cve_nist:`2020-11494`, :cve_nist:`2020-11565`, :cve_nist:`2020-11608`, :cve_nist:`2020-11609`, :cve_nist:`2020-11668`, :cve_nist:`2020-11669`, :cve_nist:`2020-11884`, :cve_nist:`2020-12114`, :cve_nist:`2020-12351`, :cve_nist:`2020-12352`, :cve_nist:`2020-12362`, :cve_nist:`2020-12363`, :cve_nist:`2020-12364`, :cve_nist:`2020-12464`, :cve_nist:`2020-12465`, :cve_nist:`2020-12652`, :cve_nist:`2020-12653`, :cve_nist:`2020-12654`, :cve_nist:`2020-12655`, :cve_nist:`2020-12656`, :cve_nist:`2020-12657`, :cve_nist:`2020-12659`, :cve_nist:`2020-12768`, :cve_nist:`2020-12769`, :cve_nist:`2020-12770`, :cve_nist:`2020-12771`, :cve_nist:`2020-12826`, :cve_nist:`2020-12888`, :cve_nist:`2020-12912`, :cve_nist:`2020-13143`, :cve_nist:`2020-13974`, :cve_nist:`2020-14305`, :cve_nist:`2020-14314`, :cve_nist:`2020-14331`, :cve_nist:`2020-14351`, :cve_nist:`2020-14353`, :cve_nist:`2020-14356`, :cve_nist:`2020-14381`, :cve_nist:`2020-14385`, :cve_nist:`2020-14386`, :cve_nist:`2020-14390`, :cve_nist:`2020-14416`, :cve_nist:`2020-15393`, :cve_nist:`2020-15436`, :cve_nist:`2020-15437`, :cve_nist:`2020-15780`, :cve_nist:`2020-15852`, :cve_nist:`2020-16119`, :cve_nist:`2020-16120`, :cve_nist:`2020-16166`, :cve_nist:`2020-1749`, :cve_nist:`2020-24394`, :cve_nist:`2020-24490`, :cve_nist:`2020-24504`, :cve_nist:`2020-24586`, :cve_nist:`2020-24587`, :cve_nist:`2020-24588`, :cve_nist:`2020-25211`, :cve_nist:`2020-25212`, :cve_nist:`2020-25221`, :cve_nist:`2020-25284`, :cve_nist:`2020-25285`, :cve_nist:`2020-25639`, :cve_nist:`2020-25641`, :cve_nist:`2020-25643`, :cve_nist:`2020-25645`, :cve_nist:`2020-25656`, :cve_nist:`2020-25668`, :cve_nist:`2020-25669`, :cve_nist:`2020-25670`, :cve_nist:`2020-25671`, :cve_nist:`2020-25672`, :cve_nist:`2020-25673`, :cve_nist:`2020-25704`, :cve_nist:`2020-25705`, :cve_nist:`2020-26088`, :cve_nist:`2020-26139`, :cve_nist:`2020-26141`, :cve_nist:`2020-26145`, :cve_nist:`2020-26147`, :cve_nist:`2020-26541`, :cve_nist:`2020-26555`, :cve_nist:`2020-26558`, :cve_nist:`2020-27066`, :cve_nist:`2020-27067`, :cve_nist:`2020-27068`, :cve_nist:`2020-27152`, :cve_nist:`2020-27170`, :cve_nist:`2020-27171`, :cve_nist:`2020-27194`, :cve_nist:`2020-2732`, :cve_nist:`2020-27673`, :cve_nist:`2020-27675`, :cve_nist:`2020-27777`, :cve_nist:`2020-27784`, :cve_nist:`2020-27786`, :cve_nist:`2020-27815`, :cve_nist:`2020-27820`, :cve_nist:`2020-27825`, :cve_nist:`2020-27830`, :cve_nist:`2020-27835`, :cve_nist:`2020-28097`, :cve_nist:`2020-28374`, :cve_nist:`2020-28588`, :cve_nist:`2020-28915`, :cve_nist:`2020-28941`, :cve_nist:`2020-28974`, :cve_nist:`2020-29368`, :cve_nist:`2020-29369`, :cve_nist:`2020-29370`, :cve_nist:`2020-29371`, :cve_nist:`2020-29372`, :cve_nist:`2020-29373`, :cve_nist:`2020-29374`, :cve_nist:`2020-29534`, :cve_nist:`2020-29568`, :cve_nist:`2020-29569`, :cve_nist:`2020-29660`, :cve_nist:`2020-29661`, :cve_nist:`2020-35499`, :cve_nist:`2020-35508`, :cve_nist:`2020-35513`, :cve_nist:`2020-35519`, :cve_nist:`2020-36158`, :cve_nist:`2020-36310`, :cve_nist:`2020-36311`, :cve_nist:`2020-36312`, :cve_nist:`2020-36313`, :cve_nist:`2020-36322`, :cve_nist:`2020-36385`, :cve_nist:`2020-36386`, :cve_nist:`2020-36387`, :cve_nist:`2020-36516`, :cve_nist:`2020-36557`, :cve_nist:`2020-36558`, :cve_nist:`2020-36691`, :cve_nist:`2020-36694`, :cve_nist:`2020-36766`, :cve_nist:`2020-3702`, :cve_nist:`2020-4788`, :cve_nist:`2020-7053`, :cve_nist:`2020-8428`, :cve_nist:`2020-8647`, :cve_nist:`2020-8648`, :cve_nist:`2020-8649`, :cve_nist:`2020-8694`, :cve_nist:`2020-8834`, :cve_nist:`2020-8835`, :cve_nist:`2020-8992`, :cve_nist:`2020-9383`, :cve_nist:`2020-9391`, :cve_nist:`2021-0129`, :cve_nist:`2021-0342`, :cve_mitre:`2021-0447`, :cve_mitre:`2021-0448`, :cve_nist:`2021-0512`, :cve_nist:`2021-0605`, :cve_nist:`2021-0707`, :cve_nist:`2021-0920`, :cve_nist:`2021-0929`, :cve_nist:`2021-0935`, :cve_mitre:`2021-0937`, :cve_nist:`2021-0938`, :cve_nist:`2021-0941`, :cve_nist:`2021-1048`, :cve_nist:`2021-20177`, :cve_nist:`2021-20194`, :cve_nist:`2021-20226`, :cve_nist:`2021-20239`, :cve_nist:`2021-20261`, :cve_nist:`2021-20265`, :cve_nist:`2021-20268`, :cve_nist:`2021-20292`, :cve_nist:`2021-20317`, :cve_nist:`2021-20320`, :cve_nist:`2021-20321`, :cve_nist:`2021-20322`, :cve_nist:`2021-21781`, :cve_nist:`2021-22543`, :cve_nist:`2021-22555`, :cve_nist:`2021-22600`, :cve_nist:`2021-23133`, :cve_nist:`2021-23134`, :cve_nist:`2021-26401`, :cve_nist:`2021-26708`, :cve_nist:`2021-26930`, :cve_nist:`2021-26931`, :cve_nist:`2021-26932`, :cve_nist:`2021-27363`, :cve_nist:`2021-27364`, :cve_nist:`2021-27365`, :cve_nist:`2021-28038`, :cve_nist:`2021-28039`, :cve_nist:`2021-28375`, :cve_nist:`2021-28660`, :cve_nist:`2021-28688`, :cve_nist:`2021-28691`, :cve_nist:`2021-28711`, :cve_nist:`2021-28712`, :cve_nist:`2021-28713`, :cve_nist:`2021-28714`, :cve_nist:`2021-28715`, :cve_nist:`2021-28950`, :cve_nist:`2021-28951`, :cve_nist:`2021-28952`, :cve_nist:`2021-28964`, :cve_nist:`2021-28971`, :cve_nist:`2021-28972`, :cve_nist:`2021-29154`, :cve_nist:`2021-29155`, :cve_nist:`2021-29264`, :cve_nist:`2021-29265`, :cve_nist:`2021-29266`, :cve_nist:`2021-29646`, :cve_nist:`2021-29647`, :cve_nist:`2021-29648`, :cve_nist:`2021-29649`, :cve_nist:`2021-29650`, :cve_nist:`2021-29657`, :cve_nist:`2021-30002`, :cve_nist:`2021-30178`, :cve_nist:`2021-31440`, :cve_nist:`2021-3178`, :cve_nist:`2021-31829`, :cve_nist:`2021-31916`, :cve_nist:`2021-32078`, :cve_nist:`2021-32399`, :cve_nist:`2021-32606`, :cve_nist:`2021-33033`, :cve_nist:`2021-33034`, :cve_nist:`2021-33061`, :cve_nist:`2021-33098`, :cve_nist:`2021-33135`, :cve_nist:`2021-33200`, :cve_nist:`2021-3347`, :cve_nist:`2021-3348`, :cve_nist:`2021-33624`, :cve_nist:`2021-33655`, :cve_nist:`2021-33656`, :cve_nist:`2021-33909`, :cve_nist:`2021-3411`, :cve_nist:`2021-3428`, :cve_nist:`2021-3444`, :cve_nist:`2021-34556`, :cve_nist:`2021-34693`, :cve_nist:`2021-3483`, :cve_nist:`2021-34866`, :cve_nist:`2021-3489`, :cve_nist:`2021-3490`, :cve_nist:`2021-3491`, :cve_nist:`2021-3493`, :cve_mitre:`2021-34981`, :cve_nist:`2021-3501`, :cve_nist:`2021-35039`, :cve_nist:`2021-3506`, :cve_nist:`2021-3543`, :cve_nist:`2021-35477`, :cve_nist:`2021-3564`, :cve_nist:`2021-3573`, :cve_nist:`2021-3587`, :cve_mitre:`2021-3600`, :cve_nist:`2021-3609`, :cve_nist:`2021-3612`, :cve_nist:`2021-3635`, :cve_nist:`2021-3640`, :cve_nist:`2021-3653`, :cve_nist:`2021-3655`, :cve_nist:`2021-3656`, :cve_nist:`2021-3659`, :cve_nist:`2021-3669`, :cve_nist:`2021-3679`, :cve_nist:`2021-3715`, :cve_nist:`2021-37159`, :cve_nist:`2021-3732`, :cve_nist:`2021-3736`, :cve_nist:`2021-3739`, :cve_nist:`2021-3743`, :cve_nist:`2021-3744`, :cve_nist:`2021-3752`, :cve_nist:`2021-3753`, :cve_nist:`2021-37576`, :cve_nist:`2021-3759`, :cve_nist:`2021-3760`, :cve_nist:`2021-3764`, :cve_nist:`2021-3772`, :cve_nist:`2021-38160`, :cve_nist:`2021-38166`, :cve_nist:`2021-38198`, :cve_nist:`2021-38199`, :cve_nist:`2021-38200`, :cve_nist:`2021-38201`, :cve_nist:`2021-38202`, :cve_nist:`2021-38203`, :cve_nist:`2021-38204`, :cve_nist:`2021-38205`, :cve_nist:`2021-38206`, :cve_nist:`2021-38207`, :cve_nist:`2021-38208`, :cve_nist:`2021-38209`, :cve_nist:`2021-38300`, :cve_nist:`2021-3894`, :cve_nist:`2021-3896`, :cve_nist:`2021-3923`, :cve_nist:`2021-39633`, :cve_nist:`2021-39634`, :cve_nist:`2021-39636`, :cve_nist:`2021-39648`, :cve_nist:`2021-39656`, :cve_nist:`2021-39657`, :cve_nist:`2021-39685`, :cve_nist:`2021-39686`, :cve_nist:`2021-39698`, :cve_nist:`2021-39711`, :cve_nist:`2021-39713`, :cve_nist:`2021-39714`, :cve_nist:`2021-4001`, :cve_nist:`2021-4002`, :cve_nist:`2021-4023`, :cve_nist:`2021-4028`, :cve_nist:`2021-4032`, :cve_nist:`2021-4037`, :cve_nist:`2021-40490`, :cve_nist:`2021-4083`, :cve_nist:`2021-4090`, :cve_nist:`2021-4093`, :cve_nist:`2021-4095`, :cve_nist:`2021-41073`, :cve_nist:`2021-4135`, :cve_nist:`2021-4148`, :cve_nist:`2021-4149`, :cve_nist:`2021-4150`, :cve_nist:`2021-4154`, :cve_nist:`2021-4155`, :cve_nist:`2021-4157`, :cve_nist:`2021-4159`, :cve_nist:`2021-41864`, :cve_nist:`2021-4197`, :cve_nist:`2021-42008`, :cve_nist:`2021-4202`, :cve_nist:`2021-4203`, :cve_nist:`2021-4204`, :cve_nist:`2021-4218`, :cve_nist:`2021-42252`, :cve_nist:`2021-42327`, :cve_nist:`2021-42739`, :cve_nist:`2021-43056`, :cve_nist:`2021-43057`, :cve_nist:`2021-43267`, :cve_nist:`2021-43389`, :cve_nist:`2021-43975`, :cve_nist:`2021-43976`, :cve_nist:`2021-44733`, :cve_nist:`2021-44879`, :cve_nist:`2021-45095`, :cve_nist:`2021-45100`, :cve_nist:`2021-45402`, :cve_nist:`2021-45469`, :cve_nist:`2021-45480`, :cve_nist:`2021-45485`, :cve_nist:`2021-45486`, :cve_nist:`2021-45868`, :cve_nist:`2021-46283`, :cve_nist:`2022-0001`, :cve_nist:`2022-0002`, :cve_nist:`2022-0168`, :cve_nist:`2022-0171`, :cve_nist:`2022-0185`, :cve_nist:`2022-0264`, :cve_nist:`2022-0286`, :cve_nist:`2022-0322`, :cve_nist:`2022-0330`, :cve_nist:`2022-0382`, :cve_nist:`2022-0433`, :cve_nist:`2022-0435`, :cve_nist:`2022-0480`, :cve_nist:`2022-0487`, :cve_nist:`2022-0492`, :cve_nist:`2022-0494`, :cve_nist:`2022-0500`, :cve_nist:`2022-0516`, :cve_nist:`2022-0617`, :cve_nist:`2022-0644`, :cve_nist:`2022-0646`, :cve_nist:`2022-0742`, :cve_nist:`2022-0812`, :cve_nist:`2022-0847`, :cve_nist:`2022-0850`, :cve_nist:`2022-0854`, :cve_nist:`2022-0995`, :cve_nist:`2022-0998`, :cve_nist:`2022-1011`, :cve_nist:`2022-1012`, :cve_nist:`2022-1015`, :cve_nist:`2022-1016`, :cve_nist:`2022-1043`, :cve_nist:`2022-1048`, :cve_nist:`2022-1055`, :cve_nist:`2022-1158`, :cve_nist:`2022-1184`, :cve_nist:`2022-1195`, :cve_nist:`2022-1198`, :cve_nist:`2022-1199`, :cve_nist:`2022-1204`, :cve_nist:`2022-1205`, :cve_nist:`2022-1263`, :cve_nist:`2022-1280`, :cve_nist:`2022-1353`, :cve_nist:`2022-1419`, :cve_nist:`2022-1462`, :cve_nist:`2022-1508`, :cve_nist:`2022-1516`, :cve_nist:`2022-1651`, :cve_nist:`2022-1652`, :cve_nist:`2022-1671`, :cve_nist:`2022-1678`, :cve_nist:`2022-1679`, :cve_nist:`2022-1729`, :cve_nist:`2022-1734`, :cve_nist:`2022-1786`, :cve_nist:`2022-1789`, :cve_nist:`2022-1836`, :cve_nist:`2022-1852`, :cve_nist:`2022-1882`, :cve_nist:`2022-1943`, :cve_nist:`2022-1966`, :cve_nist:`2022-1972`, :cve_nist:`2022-1973`, :cve_nist:`2022-1974`, :cve_nist:`2022-1975`, :cve_nist:`2022-1976`, :cve_nist:`2022-1998`, :cve_nist:`2022-20008`, :cve_nist:`2022-20132`, :cve_nist:`2022-20141`, :cve_nist:`2022-20148`, :cve_nist:`2022-20153`, :cve_nist:`2022-20154`, :cve_nist:`2022-20158`, :cve_nist:`2022-20166`, :cve_nist:`2022-20368`, :cve_nist:`2022-20369`, :cve_nist:`2022-20409`, :cve_nist:`2022-20421`, :cve_nist:`2022-20422`, :cve_nist:`2022-20423`, :cve_nist:`2022-20424`, :cve_mitre:`2022-20565`, :cve_nist:`2022-20566`, :cve_nist:`2022-20567`, :cve_nist:`2022-20568`, :cve_nist:`2022-20572`, :cve_nist:`2022-2078`, :cve_nist:`2022-21123`, :cve_nist:`2022-21125`, :cve_nist:`2022-21166`, :cve_nist:`2022-21385`, :cve_nist:`2022-21499`, :cve_mitre:`2022-21505`, :cve_nist:`2022-2153`, :cve_nist:`2022-2196`, :cve_mitre:`2022-22942`, :cve_nist:`2022-23036`, :cve_nist:`2022-23037`, :cve_nist:`2022-23038`, :cve_nist:`2022-23039`, :cve_nist:`2022-23040`, :cve_nist:`2022-23041`, :cve_nist:`2022-23042`, :cve_nist:`2022-2308`, :cve_nist:`2022-2318`, :cve_nist:`2022-23222`, :cve_nist:`2022-2327`, :cve_nist:`2022-2380`, :cve_nist:`2022-23816`, :cve_nist:`2022-23960`, :cve_nist:`2022-24122`, :cve_nist:`2022-24448`, :cve_nist:`2022-24958`, :cve_nist:`2022-24959`, :cve_nist:`2022-2503`, :cve_nist:`2022-25258`, :cve_nist:`2022-25375`, :cve_nist:`2022-25636`, :cve_mitre:`2022-2585`, :cve_mitre:`2022-2586`, :cve_mitre:`2022-2588`, :cve_nist:`2022-2590`, :cve_mitre:`2022-2602`, :cve_nist:`2022-26365`, :cve_nist:`2022-26373`, :cve_nist:`2022-2639`, :cve_nist:`2022-26490`, :cve_nist:`2022-2663`, :cve_nist:`2022-26966`, :cve_nist:`2022-27223`, :cve_nist:`2022-27666`, :cve_nist:`2022-27672`, :cve_nist:`2022-2785`, :cve_nist:`2022-27950`, :cve_nist:`2022-28356`, :cve_nist:`2022-28388`, :cve_nist:`2022-28389`, :cve_nist:`2022-28390`, :cve_nist:`2022-2873`, :cve_nist:`2022-28796`, :cve_nist:`2022-28893`, :cve_nist:`2022-2905`, :cve_nist:`2022-29156`, :cve_nist:`2022-2938`, :cve_nist:`2022-29581`, :cve_nist:`2022-29582`, :cve_nist:`2022-2959`, :cve_nist:`2022-2964`, :cve_nist:`2022-2977`, :cve_nist:`2022-2978`, :cve_nist:`2022-29900`, :cve_nist:`2022-29901`, :cve_nist:`2022-2991`, :cve_nist:`2022-29968`, :cve_nist:`2022-3028`, :cve_nist:`2022-30594`, :cve_nist:`2022-3061`, :cve_nist:`2022-3077`, :cve_nist:`2022-3078`, :cve_nist:`2022-3103`, :cve_nist:`2022-3104`, :cve_nist:`2022-3105`, :cve_nist:`2022-3106`, :cve_nist:`2022-3107`, :cve_nist:`2022-3108`, :cve_nist:`2022-3110`, :cve_nist:`2022-3111`, :cve_nist:`2022-3112`, :cve_nist:`2022-3113`, :cve_nist:`2022-3114`, :cve_nist:`2022-3115`, :cve_nist:`2022-3169`, :cve_nist:`2022-3170`, :cve_nist:`2022-3176`, :cve_nist:`2022-3202`, :cve_nist:`2022-32250`, :cve_nist:`2022-32296`, :cve_nist:`2022-3239`, :cve_nist:`2022-32981`, :cve_nist:`2022-3303`, :cve_nist:`2022-3344`, :cve_nist:`2022-33740`, :cve_nist:`2022-33741`, :cve_nist:`2022-33742`, :cve_nist:`2022-33743`, :cve_nist:`2022-33744`, :cve_nist:`2022-33981`, :cve_nist:`2022-3424`, :cve_nist:`2022-3435`, :cve_nist:`2022-34494`, :cve_nist:`2022-34495`, :cve_nist:`2022-34918`, :cve_nist:`2022-3521`, :cve_nist:`2022-3522`, :cve_nist:`2022-3524`, :cve_nist:`2022-3526`, :cve_nist:`2022-3531`, :cve_nist:`2022-3532`, :cve_nist:`2022-3534`, :cve_nist:`2022-3535`, :cve_nist:`2022-3541`, :cve_nist:`2022-3542`, :cve_nist:`2022-3543`, :cve_nist:`2022-3545`, :cve_nist:`2022-3564`, :cve_nist:`2022-3565`, :cve_nist:`2022-3577`, :cve_nist:`2022-3586`, :cve_nist:`2022-3594`, :cve_nist:`2022-3595`, :cve_nist:`2022-36123`, :cve_nist:`2022-3619`, :cve_nist:`2022-3621`, :cve_nist:`2022-3623`, :cve_nist:`2022-3624`, :cve_nist:`2022-3625`, :cve_nist:`2022-3628`, :cve_nist:`2022-36280`, :cve_nist:`2022-3629`, :cve_nist:`2022-3630`, :cve_nist:`2022-3633`, :cve_nist:`2022-3635`, :cve_nist:`2022-3636`, :cve_nist:`2022-3640`, :cve_nist:`2022-3643`, :cve_nist:`2022-3646`, :cve_nist:`2022-3649`, :cve_nist:`2022-36879`, :cve_nist:`2022-36946`, :cve_nist:`2022-3707`, :cve_nist:`2022-38457`, :cve_nist:`2022-3903`, :cve_nist:`2022-3910`, :cve_nist:`2022-39188`, :cve_nist:`2022-39189`, :cve_nist:`2022-39190`, :cve_nist:`2022-3977`, :cve_nist:`2022-39842`, :cve_nist:`2022-40133`, :cve_nist:`2022-40307`, :cve_nist:`2022-40476`, :cve_nist:`2022-40768`, :cve_nist:`2022-4095`, :cve_nist:`2022-40982`, :cve_nist:`2022-41218`, :cve_nist:`2022-41222`, :cve_nist:`2022-4127`, :cve_nist:`2022-4128`, :cve_nist:`2022-4129`, :cve_nist:`2022-4139`, :cve_nist:`2022-41674`, :cve_nist:`2022-41849`, :cve_nist:`2022-41850`, :cve_nist:`2022-41858`, :cve_nist:`2022-42328`, :cve_nist:`2022-42329`, :cve_nist:`2022-42432`, :cve_nist:`2022-4269`, :cve_nist:`2022-42703`, :cve_nist:`2022-42719`, :cve_nist:`2022-42720`, :cve_nist:`2022-42721`, :cve_nist:`2022-42722`, :cve_nist:`2022-42895`, :cve_nist:`2022-42896`, :cve_nist:`2022-43750`, :cve_nist:`2022-4378`, :cve_nist:`2022-4379`, :cve_nist:`2022-4382`, :cve_nist:`2022-43945`, :cve_nist:`2022-45869`, :cve_nist:`2022-45886`, :cve_nist:`2022-45887`, :cve_nist:`2022-45919`, :cve_nist:`2022-45934`, :cve_nist:`2022-4662`, :cve_nist:`2022-4696`, :cve_nist:`2022-4744`, :cve_nist:`2022-47518`, :cve_nist:`2022-47519`, :cve_nist:`2022-47520`, :cve_nist:`2022-47521`, :cve_nist:`2022-47929`, :cve_nist:`2022-47938`, :cve_nist:`2022-47939`, :cve_nist:`2022-47940`, :cve_nist:`2022-47941`, :cve_nist:`2022-47942`, :cve_nist:`2022-47943`, :cve_nist:`2022-47946`, :cve_nist:`2022-4842`, :cve_nist:`2022-48423`, :cve_nist:`2022-48424`, :cve_nist:`2022-48425`, :cve_nist:`2022-48502`, :cve_nist:`2023-0030`, :cve_nist:`2023-0045`, :cve_nist:`2023-0047`, :cve_nist:`2023-0122`, :cve_nist:`2023-0160`, :cve_nist:`2023-0179`, :cve_nist:`2023-0210`, :cve_nist:`2023-0240`, :cve_nist:`2023-0266`, :cve_nist:`2023-0386`, :cve_nist:`2023-0394`, :cve_nist:`2023-0458`, :cve_nist:`2023-0459`, :cve_nist:`2023-0461`, :cve_nist:`2023-0468`, :cve_nist:`2023-0469`, :cve_nist:`2023-0590`, :cve_nist:`2023-0615`, :cve_mitre:`2023-1032`, :cve_nist:`2023-1073`, :cve_nist:`2023-1074`, :cve_nist:`2023-1076`, :cve_nist:`2023-1077`, :cve_nist:`2023-1078`, :cve_nist:`2023-1079`, :cve_nist:`2023-1095`, :cve_nist:`2023-1118`, :cve_nist:`2023-1192`, :cve_nist:`2023-1194`, :cve_nist:`2023-1195`, :cve_nist:`2023-1206`, :cve_nist:`2023-1249`, :cve_nist:`2023-1252`, :cve_nist:`2023-1281`, :cve_nist:`2023-1295`, :cve_nist:`2023-1380`, :cve_nist:`2023-1382`, :cve_nist:`2023-1390`, :cve_nist:`2023-1513`, :cve_nist:`2023-1582`, :cve_nist:`2023-1583`, :cve_nist:`2023-1611`, :cve_nist:`2023-1637`, :cve_nist:`2023-1652`, :cve_nist:`2023-1670`, :cve_nist:`2023-1829`, :cve_nist:`2023-1838`, :cve_nist:`2023-1855`, :cve_nist:`2023-1859`, :cve_nist:`2023-1872`, :cve_nist:`2023-1989`, :cve_nist:`2023-1990`, :cve_nist:`2023-1998`, :cve_nist:`2023-2002`, :cve_nist:`2023-2006`, :cve_nist:`2023-2007`, :cve_nist:`2023-2008`, :cve_nist:`2023-2019`, :cve_nist:`2023-20569`, :cve_nist:`2023-20588`, :cve_nist:`2023-20593`, :cve_nist:`2023-20928`, :cve_nist:`2023-20938`, :cve_nist:`2023-21102`, :cve_nist:`2023-21106`, :cve_nist:`2023-2124`, :cve_nist:`2023-21255`, :cve_nist:`2023-2156`, :cve_nist:`2023-2162`, :cve_nist:`2023-2163`, :cve_nist:`2023-2166`, :cve_nist:`2023-2177`, :cve_nist:`2023-2194`, :cve_nist:`2023-2235`, :cve_nist:`2023-2236`, :cve_nist:`2023-2248`, :cve_nist:`2023-2269`, :cve_nist:`2023-22995`, :cve_nist:`2023-22996`, :cve_nist:`2023-22997`, :cve_nist:`2023-22998`, :cve_nist:`2023-22999`, :cve_nist:`2023-23000`, :cve_nist:`2023-23001`, :cve_nist:`2023-23002`, :cve_nist:`2023-23003`, :cve_nist:`2023-23004`, :cve_nist:`2023-23006`, :cve_nist:`2023-23454`, :cve_nist:`2023-23455`, :cve_nist:`2023-23559`, :cve_nist:`2023-23586`, :cve_nist:`2023-2430`, :cve_nist:`2023-2483`, :cve_nist:`2023-25012`, :cve_nist:`2023-2513`, :cve_nist:`2023-25775`, :cve_nist:`2023-2598`, :cve_nist:`2023-26544`, :cve_nist:`2023-26545`, :cve_nist:`2023-26605`, :cve_nist:`2023-26606`, :cve_nist:`2023-26607`, :cve_nist:`2023-28327`, :cve_nist:`2023-28328`, :cve_nist:`2023-28410`, :cve_nist:`2023-28464`, :cve_nist:`2023-28466`, :cve_nist:`2023-2860`, :cve_nist:`2023-28772`, :cve_nist:`2023-28866`, :cve_nist:`2023-2898`, :cve_nist:`2023-2985`, :cve_nist:`2023-3006`, :cve_nist:`2023-30456`, :cve_nist:`2023-30772`, :cve_nist:`2023-3090`, :cve_nist:`2023-3106`, :cve_nist:`2023-3111`, :cve_nist:`2023-3117`, :cve_nist:`2023-31248`, :cve_nist:`2023-3141`, :cve_nist:`2023-31436`, :cve_nist:`2023-3159`, :cve_nist:`2023-3161`, :cve_nist:`2023-3212`, :cve_nist:`2023-3220`, :cve_nist:`2023-32233`, :cve_nist:`2023-32247`, :cve_nist:`2023-32248`, :cve_nist:`2023-32250`, :cve_nist:`2023-32252`, :cve_nist:`2023-32254`, :cve_nist:`2023-32257`, :cve_nist:`2023-32258`, :cve_nist:`2023-32269`, :cve_nist:`2023-3268`, :cve_nist:`2023-3269`, :cve_nist:`2023-3312`, :cve_nist:`2023-3317`, :cve_nist:`2023-33203`, :cve_nist:`2023-33250`, :cve_nist:`2023-33288`, :cve_nist:`2023-3338`, :cve_nist:`2023-3355`, :cve_nist:`2023-3357`, :cve_nist:`2023-3358`, :cve_nist:`2023-3359`, :cve_nist:`2023-3389`, :cve_nist:`2023-3390`, :cve_nist:`2023-33951`, :cve_nist:`2023-33952`, :cve_nist:`2023-34255`, :cve_nist:`2023-34256`, :cve_nist:`2023-34319`, :cve_nist:`2023-3439`, :cve_nist:`2023-35001`, :cve_nist:`2023-3567`, :cve_nist:`2023-35788`, :cve_nist:`2023-35823`, :cve_nist:`2023-35824`, :cve_nist:`2023-35826`, :cve_nist:`2023-35828`, :cve_nist:`2023-35829`, :cve_nist:`2023-3609`, :cve_nist:`2023-3610`, :cve_nist:`2023-3611`, :cve_nist:`2023-37453`, :cve_nist:`2023-3772`, :cve_nist:`2023-3773`, :cve_nist:`2023-3776`, :cve_nist:`2023-3777`, :cve_nist:`2023-3812`, :cve_nist:`2023-38409`, :cve_nist:`2023-38426`, :cve_nist:`2023-38427`, :cve_nist:`2023-38428`, :cve_nist:`2023-38429`, :cve_nist:`2023-38430`, :cve_nist:`2023-38431`, :cve_nist:`2023-38432`, :cve_nist:`2023-3863`, :cve_mitre:`2023-3865`, :cve_mitre:`2023-3866`, :cve_mitre:`2023-3867`, :cve_nist:`2023-4004`, :cve_nist:`2023-4015`, :cve_nist:`2023-40283`, :cve_nist:`2023-4128`, :cve_nist:`2023-4132`, :cve_nist:`2023-4147`, :cve_nist:`2023-4155`, :cve_nist:`2023-4194`, :cve_nist:`2023-4206`, :cve_nist:`2023-4207`, :cve_nist:`2023-4208`, :cve_nist:`2023-4273`, :cve_nist:`2023-42752`, :cve_nist:`2023-42753`, :cve_nist:`2023-4385`, :cve_nist:`2023-4387`, :cve_nist:`2023-4389`, :cve_nist:`2023-4394`, :cve_nist:`2023-4459`, :cve_nist:`2023-4569`, :cve_nist:`2023-4611` and :cve_nist:`2023-4623`
-  nghttp2: Fix :cve_nist:`2023-35945`
-  openssl: Fix :cve_nist:`2023-2975`, :cve_nist:`2023-3446`, :cve_nist:`2023-3817`, :cve_nist:`2023-4807` and :cve_nist:`2023-5363`
-  pixman: Ignore :cve_nist:`2023-37769`
-  procps: Fix :cve_nist:`2023-4016`
-  python3-git: Fix :cve_nist:`2023-40267`, :cve_nist:`2023-40590` and :cve_nist:`2023-41040`
-  python3-pygments: Fix :cve_nist:`2022-40896`
-  python3-urllib3: Fix :cve_nist:`2023-43804` and :cve_nist:`2023-45803`
-  python3: Fix :cve_nist:`2023-24329` and :cve_nist:`2023-40217`
-  qemu: Fix :cve_nist:`2023-3180`, :cve_nist:`2023-3354` and :cve_nist:`2023-42467`
-  qemu: Ignore :cve_nist:`2023-2680`
-  screen: Fix :cve_nist:`2023-24626`
-  shadow: Fix :cve_mitre:`2023-4641`
-  tiff: Fix :cve_nist:`2023-40745` and :cve_nist:`2023-41175`
-  vim: Fix :cve_nist:`2023-3896`, :cve_nist:`2023-4733`, :cve_nist:`2023-4734`, :cve_nist:`2023-4735`, :cve_nist:`2023-4736`, :cve_nist:`2023-4738`, :cve_nist:`2023-4750`, :cve_nist:`2023-4752`, :cve_nist:`2023-4781`, :cve_nist:`2023-5441` and :cve_nist:`2023-5535`
-  webkitgtk: Fix :cve_nist:`2023-32435` and :cve_nist:`2023-32439`
-  xserver-xorg: Fix :cve_nist:`2023-5367` and :cve_nist:`2023-5380`


Fixes in Yocto-4.2.4
~~~~~~~~~~~~~~~~~~~~

-  README: Update to point to new contributor guide
-  README: fix mail address in git example command
-  SECURITY.md: Add file
-  avahi: handle invalid service types gracefully
-  bind: upgrade to 9.18.19
-  bitbake.conf: add bunzip2 in :term:`HOSTTOOLS`
-  bitbake: Fix disk space monitoring on cephfs
-  bitbake: SECURITY.md: add file
-  brief-yoctoprojectqs: use new CDN mirror for sstate
-  bsp-guide: bsp.rst: replace reference to wiki
-  bsp-guide: bsp: skip Intel machines no longer supported in Poky
-  build-appliance-image: Update to mickledore head revision
-  build-sysroots: Add :term:`SUMMARY` field
-  build-sysroots: Ensure dependency chains are minimal
-  build-sysroots: target or native sysroot population need to be selected explicitly
-  buildtools-tarball: Add libacl
-  busybox: Set PATH in syslog initscript
-  busybox: remove coreutils dependency in busybox-ptest
-  cmake.bbclass: fix allarch override syntax
-  cml1: Fix KCONFIG_CONFIG_COMMAND not conveyed fully in do_menuconfig
-  contributor-guide/style-guide: Add a note about task idempotence
-  contributor-guide/style-guide: Refer to recipes, not packages
-  contributor-guide: deprecate "Accepted" patch status
-  contributor-guide: discourage marking patches as Inappropriate
-  contributor-guide: recipe-style-guide: add Upstream-Status
-  contributor-guide: recipe-style-guide: add more patch tagging examples
-  contributor-guide: recipe-style-guide: add section about CVE patches
-  contributor-guide: style-guide: discourage using Pending patch status
-  core-image-ptest: Define a fallback for :term:`SUMMARY` field
-  cve-check: add CVSS vector string to CVE database and reports
-  cve-check: don't warn if a patch is remote
-  cve-check: slightly more verbose warning when adding the same package twice
-  cve-check: sort the package list in the JSON report
-  cve-exclusion_6.1.inc: update for 6.1.57
-  dbus: add additional entries to :term:`CVE_PRODUCT`
-  dbus: upgrade to 1.14.10
-  dev-manual: add security team processes
-  dev-manual: disk-space: improve wording for obsolete sstate cache files
-  dev-manual: disk-space: mention faster "find" command to trim sstate cache
-  dev-manual: fix testimage usage instructions
-  dev-manual: layers: Add notes about layer.conf
-  dev-manual: licenses: mention :term:`SPDX` for license compliance
-  dev-manual: new-recipe.rst fix inconsistency with contributor guide
-  dev-manual: new-recipe.rst: add missing parenthesis to "Patching Code" section
-  dev-manual: new-recipe.rst: replace reference to wiki
-  dev-manual: remove unsupported :term: markup inside markup
-  dev-manual: start.rst: remove obsolete reference
-  ell: upgrade to 0.58
-  externalsrc: fix dependency chain issues
-  ffmpeg: upgrade to 5.1.3
-  ffmpeg: avoid neon on unsupported machines
-  file: fix call to localtime_r()
-  file: upgrade to 5.45
-  fontcache.bbclass: avoid native recipes depending on target fontconfig
-  gcc-crosssdk: ignore MULTILIB_VARIANTS in signature computation
-  gcc-runtime: remove bashism
-  gcc: backport a fix for ICE caused by CVE-2023-4039.patch
-  gcc: depend on zstd
-  gdb: fix :term:`RDEPENDS` for PACKAGECONFIG[tui]
-  glib-2.0: libelf has a configure option now, specify it
-  glibc: stable 2.37 branch updates
-  gnupg: Fix reproducibility failure
-  gnupg: upgrade to 2.4.3
-  go: upgrade to 1.20.7
-  graphene: fix runtime detection of IEEE754 behaviour
-  gstreamer: upgrade to 1.22.6
-  gtk4: upgrade to 4.10.5
-  gzip: upgrade to 1.13
-  igt-gpu-tools: do not write shortened git commit hash into binaries
-  inetutils: don't guess target paths
-  inetutils: remove obsolete cruft from do_configure
-  insane.bbclass: Count raw bytes in shebang-size
-  kernel.bbclass: Add force flag to rm calls
-  lib/package_manager: Improve repo artefact filtering
-  libc-test: Run as non-root user
-  libconvert-asn1-perl: upgrade to 0.34
-  libevent: fix patch Upstream-Status
-  libgudev: explicitly disable tests and vapi
-  librepo: upgrade to 1.15.2
-  librsvg: upgrade to 2.54.6
-  libsndfile1: upgrade to 1.2.2
-  libsoup-2.4: Only specify --cross-file when building for target
-  libsoup-2.4: update :term:`PACKAGECONFIG`
-  libx11: upgrade to 1.8.7
-  libxkbcommon: add :term:`CVE_PRODUCT`
-  libxpm: upgrade to 3.5.17
-  linux-firmware: add firmware files for NXP BT chipsets
-  linux-firmware: package Dragonboard 845c sensors DSP firmware
-  linux-firmware: package audio topology for Lenovo X13s
-  linux-firmware: upgrade to 20230804
-  linux-yocto/5.15: update to v5.15.133
-  linux-yocto/6.1: fix CONFIG_F2FS_IO_TRACE configuration warning
-  linux-yocto/6.1: fix IRQ-80 warnings
-  linux-yocto/6.1: fix uninitialized read in nohz_full/isolcpus setup
-  linux-yocto/6.1: tiny: fix arm 32 boot
-  linux-yocto/6.1: update to v6.1.57
-  linux-yocto: add script to generate kernel :term:`CVE_CHECK_IGNORE` entries
-  linux-yocto: make sure the pahole-native available before do_kernel_configme
-  linux/cve-exclusion: add generated CVE_CHECK_IGNOREs
-  linux/generate-cve-exclusions: fix mishandling of boundary values
-  linux/generate-cve-exclusions: print the generated time in UTC
-  manuals: add new contributor guide
-  manuals: correct "yocto-linux" by "linux-yocto"
-  mdadm: Disable further tests due to intermittent failures
-  mdadm: skip running 04update-uuid and 07revert-inplace testcases
-  migration-guides: add release notes for 4.0.12
-  migration-guides: add release notes for 4.0.13
-  migration-guides: add release notes for 4.2.3
-  mpfr: upgrade to 4.2.1
-  multilib.conf: explicitly make MULTILIB_VARIANTS vardeps on MULTILIBS
-  nativesdk-intercept: Fix bad intercept chgrp/chown logic
-  nettle: avoid neon on unsupported machines
-  oe-depends-dot: improve '-w' behavior
-  oeqa dnf_runtime.py: fix HTTP server IP address and port
-  oeqa selftest context.py: remove warning from missing meta-selftest
-  oeqa selftest context.py: whitespace fix
-  oeqa/concurrencytest: Remove invalid buffering option
-  oeqa/selftest/context.py: check git command return values
-  oeqa/selftest/wic: Improve assertTrue calls
-  oeqa/selftest: Fix broken symlink removal handling
-  oeqa/utils/gitarchive: Handle broken commit counts in results repo
-  openssl: upgrade to 3.1.4
-  openssl: build and install manpages only if they are enabled
-  openssl: ensure all ptest fails are caught
-  openssl: parallelize tests
-  overview: Add note about non-reproducibility side effects
-  packages.bbclass: Correct the check for conflicts with renamed packages
-  pango: explictly enable/disable libthai
-  patch.py: use --absolute-git-dir instead of --show-toplevel to retrieve gitdir
-  pixman: Remove duplication of license MIT
-  pixman: avoid neon on unsupported machines
-  poky.conf: bump version for 4.2.4 release
-  profile-manual: aesthetic cleanups
-  pseudo: Fix to work with glibc 2.38
-  ptest: report tests that were killed on timeout
-  python3-git: upgrade to 3.1.37
-  python3-urllib3: update to v1.26.18
-  python3: upgrade to 3.11.5
-  qemu: fix "Bad FPU state detected" fault on qemu-system-i386
-  ref-manual: Fix :term:`PACKAGECONFIG` term and add an example
-  ref-manual: Warn about :term:`COMPATIBLE_MACHINE` skipping native recipes
-  ref-manual: point outdated link to the new location
-  ref-manual: releases.svg: Scarthgap is now version 5.0
-  ref-manual: system-requirements: update supported distros
-  ref-manual: variables: add :term:`RECIPE_SYSROOT` and :term:`RECIPE_SYSROOT_NATIVE`
-  ref-manual: variables: add :term:`TOOLCHAIN_OPTIONS` variable
-  ref-manual: variables: add example for :term:`SYSROOT_DIRS` variable
-  ref-manual: variables: provide no-match example for :term:`COMPATIBLE_MACHINE`
-  resulttool/report: Avoid divide by zero
-  runqemu: check permissions of available render nodes as well as their presence
-  screen: upgrade to 4.9.1
-  scripts/create-pull-request: update URLs to git repositories
-  sdk-manual: appendix-obtain: improve and update descriptions
-  sdk-manual: extensible.rst: fix multiple formatting issues
-  shadow: fix patch Upstream-Status
-  strace: parallelize ptest
-  sudo: upgrade to 1.9.15p2
-  systemd-bootchart: musl fixes have been rejected upstream
-  systemd: backport patch to fix warning in systemd-vconsole-setup
-  tar: upgrade to 1.35
-  tcl: Add a way to skip ptests
-  tcl: prevent installing another copy of tzdata
-  template: fix typo in section header
-  test-manual: reproducible-builds: stop mentioning LTO bug
-  uboot-extlinux-config.bbclass: fix missed override syntax migration
-  vim: upgrade to 9.0.2048
-  vim: update obsolete comment
-  wayland-utils: add libdrm :term:`PACKAGECONFIG`
-  weston-init: fix init code indentation
-  weston-init: remove misleading comment about udev rule
-  wic: bootimg-partition: Fix file name in debug message
-  wic: fix wrong attempt to create file system in upartitioned regions
-  wireless-regdb: upgrade to 2023.09.01
-  xz: upgrade to 5.4.4
-  yocto-uninative: Update to 4.2 for glibc 2.38
-  yocto-uninative: Update to 4.3


Known Issues in Yocto-4.2.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

- N/A


Contributors to Yocto-4.2.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Alberto Planas
-  Alexander Kanavin
-  Alexis Lothoré
-  Antoine Lubineau
-  Anuj Mittal
-  Archana Polampalli
-  Arne Schwerdt
-  BELHADJ SALEM Talel
-  Benjamin Bara
-  Bruce Ashfield
-  Chen Qi
-  Colin McAllister
-  Daniel Semkowicz
-  Dmitry Baryshkov
-  Eilís 'pidge' Ní Fhlannagáin
-  Emil Kronborg Andersen
-  Etienne Cordonnier
-  Jaeyoon Jung
-  Jan Garcia
-  Joe Slater
-  Joshua Watt
-  Julien Stephan
-  Kai Kang
-  Khem Raj
-  Lee Chee Yang
-  Markus Niebel
-  Markus Volk
-  Marta Rybczynska
-  Martijn de Gouw
-  Martin Jansa
-  Michael Halstead
-  Michael Opdenacker
-  Mikko Rapeli
-  Mingli Yu
-  Narpat Mali
-  Otavio Salvador
-  Ovidiu Panait
-  Peter Kjellerstedt
-  Peter Marko
-  Peter Suti
-  Poonam Jadhav
-  Quentin Schulz
-  Richard Purdie
-  Robert P. J. Day
-  Roland Hieber
-  Ross Burton
-  Ryan Eatmon
-  Sakib Sajal
-  Samantha Jalabert
-  Sanjana
-  Sanjay Chitroda
-  Sean Nyekjaer
-  Siddharth Doshi
-  Soumya Sambu
-  Stefan Tauner
-  Steve Sakoman
-  Tan Wen Yan
-  Tom Hochstein
-  Trevor Gamblin
-  Vijay Anusuri
-  Wang Mingyu
-  Xiangyu Chen
-  Yash Shinde
-  Yoann Congal
-  Yogita Urade
-  Yuta Hayama


Repositories / Downloads for Yocto-4.2.4
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

poky

-  Repository Location: :yocto_git:`/poky`
-  Branch: :yocto_git:`mickledore </poky/log/?h=mickledore>`
-  Tag:  :yocto_git:`yocto-4.2.4 </poky/log/?h=yocto-4.2.4>`
-  Git Revision: :yocto_git:`7235399a86b134e57d5eb783d7f1f57ca0439ae5 </poky/commit/?id=7235399a86b134e57d5eb783d7f1f57ca0439ae5>`
-  Release Artefact: poky-7235399a86b134e57d5eb783d7f1f57ca0439ae5
-  sha: 3d56bb4232ab29ae18249529856f0e638c50c764fc495d6beb1ecd295fa5e5e3
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2.4/poky-7235399a86b134e57d5eb783d7f1f57ca0439ae5.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2.4/poky-7235399a86b134e57d5eb783d7f1f57ca0439ae5.tar.bz2

openembedded-core

-  Repository Location: :oe_git:`/openembedded-core`
-  Branch: :oe_git:`mickledore </openembedded-core/log/?h=mickledore>`
-  Tag:  :oe_git:`yocto-4.2.4 </openembedded-core/log/?h=yocto-4.2.4>`
-  Git Revision: :oe_git:`23b5141400b2c676c806df3308f023f7c04e34e0 </openembedded-core/commit/?id=23b5141400b2c676c806df3308f023f7c04e34e0>`
-  Release Artefact: oecore-23b5141400b2c676c806df3308f023f7c04e34e0
-  sha: 152f4ee3cdd2e159f6bd34b01d517de44dfe670d35a5e3c84cc32ee7842d9741
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2.4/oecore-23b5141400b2c676c806df3308f023f7c04e34e0.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2.4/oecore-23b5141400b2c676c806df3308f023f7c04e34e0.tar.bz2

meta-mingw

-  Repository Location: :yocto_git:`/meta-mingw`
-  Branch: :yocto_git:`mickledore </meta-mingw/log/?h=mickledore>`
-  Tag:  :yocto_git:`yocto-4.2.4 </meta-mingw/log/?h=yocto-4.2.4>`
-  Git Revision: :yocto_git:`d87d4f00b9c6068fff03929a4b0f231a942d3873 </meta-mingw/commit/?id=d87d4f00b9c6068fff03929a4b0f231a942d3873>`
-  Release Artefact: meta-mingw-d87d4f00b9c6068fff03929a4b0f231a942d3873
-  sha: 8036847cf5bf3da9db4bad13aac9080d559848679f0ae03694d55a576bcaf75f
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2.4/meta-mingw-d87d4f00b9c6068fff03929a4b0f231a942d3873.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2.4/meta-mingw-d87d4f00b9c6068fff03929a4b0f231a942d3873.tar.bz2

bitbake

-  Repository Location: :oe_git:`/bitbake`
-  Branch: :oe_git:`2.4 </bitbake/log/?h=2.4>`
-  Tag:  :oe_git:`yocto-4.2.4 </bitbake/log/?h=yocto-4.2.4>`
-  Git Revision: :oe_git:`c7e094ec3beccef0bbbf67c100147c449d9c6836 </bitbake/commit/?id=c7e094ec3beccef0bbbf67c100147c449d9c6836>`
-  Release Artefact: bitbake-c7e094ec3beccef0bbbf67c100147c449d9c6836
-  sha: 6a35a62bee3446cd0f9e0ec1de9b8f60fc396109075b37d7c4a1f2e6d63271c6
-  Download Locations:
   http://downloads.yoctoproject.org/releases/yocto/yocto-4.2.4/bitbake-c7e094ec3beccef0bbbf67c100147c449d9c6836.tar.bz2
   http://mirrors.kernel.org/yocto/yocto/yocto-4.2.4/bitbake-c7e094ec3beccef0bbbf67c100147c449d9c6836.tar.bz2

yocto-docs

-  Repository Location: :yocto_git:`/yocto-docs`
-  Branch: :yocto_git:`mickledore </yocto-docs/log/?h=mickledore>`
-  Tag: :yocto_git:`yocto-4.2.4 </yocto-docs/log/?h=yocto-4.2.4>`
-  Git Revision: :yocto_git:`91a29ca94314c87fd3dc68601cd4932bdfffde35 </yocto-docs/commit/?id=91a29ca94314c87fd3dc68601cd4932bdfffde35>`

