EasyCall
========

This is a simple Android app for my mother to help her utilize the "smart" phone I bought her.
With this app, she can send text messages or make phone calls within two clicks.

Main functions:
1. click to make phone calls to target phone.
2. edit target phone number.
3. select short message template from spinner, click to send.
4. add/delete short messages.

Additional functions:
1. check phone this.phone.number and this.phone.IMEI at start. If this.phone.number changed,
reactivition is required; if this.phone.IMEI changed, quit after a alerting dialog.
2. set app background paper randomly at runtime.
3. flip screen to change background paper.
4. long press screen to set app background paper as phone wall paper.

Anyone wants to customize this app, change the following:
1. your phone IMEI and the phone number you are going to call.
2. sync app_version with database version.
3. sync wallpaper_num in strinfs.xml with actual wallpaper number in drawable(you
   can rename the wallpapers with Rename.java)
