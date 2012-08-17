TimeSense
=============

JApplication to read data into Timestreams from SensePods. Developed as part of the [Relate project](http://horizab1.miniserver.com/relate/)

Installation
-----------

1. Download TimeSense files. You will also need the Sensaris com/sensaris files from their sensdecomp application, along with libraries for bluecove (Bluetooth), Apache commons-httpclient 3.1 and apache xmlrpc-client-3.1.3. Contact me for full library list.
2. Java compile files
3. That's it. You're ready to go!


Usage
-----

    java -jar Sensaris.jar [Timestreams url] [proxy url or "null"] [proxy port or "null"] [timestreams username] [timestreams password] [timestreams.api procedure] [measurement container label]
    
    Example: 
    java -jar Sensaris.jar http://timestreams.wp.horizon.ac.uk/xmlrpc.php wwwcache-20...uk 3128 admin ***** timestreams.add_measurement wp_1_ts_CO2_78


Testing
-------

    To be completed...


Contributing
------------

1. Fork it.
2. Create a branch (`git checkout -b my_markup`)
3. Commit your changes (`git commit -am "Added Snarkdown"`)
4. Push to the branch (`git push origin my_markup`)
5. Create an [Issue][1] with a link to your branch
6. Enjoy a refreshing drink while you wait

License
------------
Copyright (C) 2012  Jesse Blum (jesse.blum@nottingham.ac.uk)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
