# Grabber OCLC from http://www.worldcat.org/oclc/

* ##Requirements:
    - Java 8 or higher must be installed.
    - If not - download form [here](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

* ##Installation:
    1. Download or install scala build tool from [here](http://www.scala-sbt.org/download.html)
    2. Download  and unpack source code to some dir. Lets it be `./grab-olcl/`.
    3. Run in bash `cd ./grab-olcl/`
    4. Then run in bash `sbt ~run`. For the first time you should wait (5-10 minutes) till sbt downloads and compiles all the dependencies.
    5. Program will crete some file in subfolder `out`:
        - oclc_<*olcl.start*>-<*olcl.end*>.json - JSON list of objects where *olcl.start* and *olcl.end* are starting and ending oclc see ["Set up range for OLCL"](#range) below.
        - grab-oclc.log - just logging file
        - grab_oclc_all.json - grabbed data in JSON format
     
* ##Config:
    * Config file is `src/mail/resources/application.conf`
    * Info about config file format can be found [here](https://github.com/typesafehub/config#using-hocon-the-json-superset) 
    * ### Config options
        * All the options are described in `src/mail/resources/application.conf`
        * #### Set up range for OLCL <a name="range"></a> 
            * **olcl.start** - beginning of olcl range
            * **olcl.end** - end of olcl range

        * #### Control of downloading process 
            * **olcl.throttle.elements** -  number of olcl per time unit (see **olcl.throttle.per**  below)that can be fed to downloader (browser) 
            * **olcl.throttle.per** - This sets time period for which `throttle.elements` is set
            * **olcl.maximum-requests** - This defines max number of unfinished requests.
        
        * #### Setup useragent  
            * **play.ws.useragent** - This defines `USRAGENT` http request header 
    

