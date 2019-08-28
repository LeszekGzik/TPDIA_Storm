package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


import com.ibm.icu.impl.CalendarAstronomer;
import com.ibm.icu.impl.CalendarAstronomer.Ecliptic;
import com.ibm.icu.impl.CalendarAstronomer.Equatorial;

import clojure.lang.Namespace;

import org.python.core.Py;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;




public class PSRCATEntry {

	
	
	//tu powinna byæ œcie¿ka do folderu z modu³ami
	static String PyModulePath = "";
	//tu powinna byæ œcie¿ka do Jythona
	static String PyPath = "./lib/";
	Map<String, String> sourceParameters= new HashMap<String, String>();
    String sourceName;
    String JName;
    String BName;
    double refsep; 
    String[] coord; //ra dan dec respectivly

    String KEY_PSRJ;
    String KEY_PSRB;
    String KEY_RAJ;
    String KEY_DECJ;
    String KEY_ELAT;
    String KEY_ELONG;
    String KEY_P0;
    String KEY_DM;
    String KEY_F0;
	/*
    Represents a known radio source in the ANTF catalog
    file. The class is initialized using a unique name
    for a known source, i.e.,

    __init__(this,name) ,

    where name is a string such as "J0048+3412" or 
    "B0052+51".

    */

    // ******************************
    //
    // INIT FUNCTION
    //
    // ******************************

	public PSRCATEntry() {
	    	this(new String("Unknown"));
	    	
	 };
	
	/*
	 * Initialises the class with a unique name. Those
	 * pulsars named before 1993 have a 'B' name, and
	 * are typically known by this name. Pulsars
	 * discovered after 1993 usually have a 'J' name.
	 * Parameters
	 * ----------
	 * :param name: the name of the catalog entry, 
	 * usually the pulsar 'J' name or 'B' 
	 * name, i.e. "J0048+3412" or 
	 * "B0052+51".
	 * 
	 * Returns
	 * ----------
	 * N/A
	 * 
	 * Examples
	 * --------
	 * >>> entry = PSRCATEntry('J0006+1834')
	 * >>> print entry.sourceName
	 * J0006+1834
	 */
    public PSRCATEntry(String name) {
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(new File("config.txt")));
    		PyModulePath = br.readLine();
    		br.close();
    	} catch(Exception e) {}
    	
    	
        // Store source details. The 'sourceParameters' 
        // dictionary in particular, stores information
        // collected from the ATNF file, as key-value
        // pairs. The keys are specified below.
        this.sourceName = name;
        this.JName = name;
        this.BName = name;
        this.refsep = 0.0;
        this.coord = null;

        // Initialise flags, used to process data. 
        // These flags correspond to the keywords used
        // in the ATNF pulsar catalog file. They can be
        // used to access data in the 'sourceParameters'
        // dictionary.
        
        this.KEY_PSRJ = "PSRJ";
        this.KEY_PSRB = "PSRB";
        this.KEY_RAJ = "RAJ";
        this.KEY_DECJ = "DECJ";
        this.KEY_ELAT = "ELAT";
        this.KEY_ELONG = "ELONG";
        this.KEY_P0 = "P0";
        this.KEY_DM = "DM";
        this.KEY_F0 = "F0";

        // Do some source initialisation. This is required 
        // as some known sources in the ATNF catalog, are
        // missing parameters.
        
        sourceParameters.put(KEY_RAJ, "00:00:00");
        sourceParameters.put(KEY_DECJ, "00:00:00");
        sourceParameters.put(KEY_ELONG, "0");
        sourceParameters.put(KEY_ELAT, "0");
//        this.sourceParameters[this.KEY_RAJ] = "00:00:00";
//        this.sourceParameters[this.KEY_DECJ] = "00:00:00";
//        this.sourceParameters[this.KEY_ELONG] = "0";
//        this.sourceParameters[this.KEY_ELAT] = "0";
    }

    
    // ******************************
    //
    // FUNCTIONS.
    //
    // ******************************
    
    /* 
     * Processes a line of text from an ATNF catalog file. 
        The catalog stores known source details in space
        delimited lines of text. The text lines appear in 
        the following format:

        <KEY> <Value 1> <value 2> <value n> 

        For example the following is a valid ATNF string:

        DM       13.9                     1    snt97

        Each string read from the ATNF catalog file, is 
        stored in the 'sourceParameters' dictionary. The 
        parameter key can be used to access the value. In 
        the example above, we use 'DM' as a key, and store
        the three accompanying values as parameters in a 
        simple list object.

        Parameters 
        ----------
        :param line_from_file: the line of text from the
            pulsar catalog file.

        Returns
        ----------
        True if parameters were correctly read from the ATNF
        file, else False.

        Examples
        --------
        >>> entry = PSRCATEntry('J0006+1834')
        >>> print entry.sourceName
        J0006+1834
        >>> line_from_file = 'DM 12.0 6 cn95'
        >>> entry.process_atnf_formatted_line(line_from_file)
        >>> print entry.get_parameter('DM')
        12.0
     */
    
    public boolean process_atnf_formatted_line(String line_from_file) {//string or ANTF string
        
        // First some basic error checking.
        if (line_from_file == null)
            return false;
        else {
        	if(line_from_file.isEmpty())  // Empty strings are False.
        		return true;
        }
        // Split line of text on whitespace. This produces a 
        // list of string literals.
        
        String[] sub_strings = line_from_file.split("\\s+");

        // The key should be first item in the split 
        // text list.
        String key = sub_strings[0];

        // The values should form the remainder of the string
        // list, minus the key.
        String[] value = new String[sub_strings.length-1];
        System.arraycopy(sub_strings, 1, value, 0, sub_strings.length - 1);
        // Now check the key value, and do some pre-processing 
        // according to the key. This is required as the ATNF 
        // database file does not always contain complete
        // information, i.e. shortened RA and DEC values, and
        // even missing period, DM, and frequency variables.

        // Try to grab the name of the source. 
        if (key.equals(this.KEY_PSRJ)){
            this.sourceName = value[0];
            this.JName = value[0];
        }
        else if(key.equals(this.KEY_PSRB)) {
       		 	this.sourceName = value[0];
       		 	this.BName = value[0];
        }
        
        // If the text contains the right ascension (RA).
        else if (key.equals(this.KEY_RAJ)) {

            // Get the RAJ. It should be in the format:
            // 00:00:00.00
            // Here right ascension should be 
            // in hh:mm:ss.s format.
            String raj = value[0];

            // Split on the colon symbol to break it 
            // into parts.
            String[] raj_parts = raj.split(":");

            // Count the parts - there should be three if we
            // have an RA of the form 00:00:00. Else if the 
            // RA is 00:00 or 00, then there will be less 
            // than three parts. These must be corrected, 
            // since we carry out known source matching based
            // on sky location.
            int length = raj_parts.length;

            // If length is less than three, add zeroes to 
            // make it complete. This will add some inaccuracy,
            // but as the values are not in the pulsar catalog
            // anyway, it is the best we can do.
            if (length < 3) {

                if (length == 1) {

                    raj += ":00:00";  // Add mm:ss parts.
                    value[0] = raj;
                    sourceParameters.put(key, raj);
                    //this.sourceParameters[key] = value;
                }
                else {
                	if(length == 2) {
                		raj += ":00";  // Add ss parts.
                        value[0] = raj;
                        sourceParameters.put(key, raj);
                        //this.sourceParameters[key] = value;
                	}
                }
            }       
            else {
            	sourceParameters.put(key, raj);
            	//this.sourceParameters[key] = value;
            }
                
        // If the text contains declination (DEC).
        }
        else if(key.equals(this.KEY_DECJ)) {
        		
            // Get the DEC. It should be in the format:
            // +00:00:00.00 or -00:00:00.00
            // Here we have the declination described 
            // in dd:mm:ss.s format.
            String decj = value[0];

            // Split on the colon symbol to break it
            // into parts.
            String[] decj_parts = decj.split(":");

            // Count the parts - there should be three if we 
            // have a DEC of the form 00:00:00. Else if the 
            // DEC is 00:00 or 00, then there will be less 
            // than three. These must be corrected since we 
            // carry out known source matching based on sky 
            // location.
            int length = decj_parts.length;

            // If length is less than three, add zeroes to 
            // make it complete. This will add some inaccuracy,
            // but of the values are not in the pulsar catalog 
            // anyway, then this is the best we can do.
            if(length < 3) {
                if(length == 1) {

                    decj += ":00:00";  // Add mm:ss parts.
                    value[0] = decj;
                    sourceParameters.put(key, decj);
                    //this.sourceParameters[key] = value;

                }
                else
                	if (length == 2) {
                		decj += ":00";  // Add ss parts.
                		value[0] = decj;
                		sourceParameters.put(key, decj);
                		//this.sourceParameters[key] = value;
                	}
            }
            else
            {
            	sourceParameters.put(key, decj);
                //this.sourceParameters[key] = value;
            }	
            
        	}
        // P0 is the period in seconds.
	    else if(key.equals(this.KEY_P0)) {
	   		// Here frequency is automatically computed from
	   		// the period.
	
	   		if(Double.valueOf(value[0]) == 0)
	   		{
	   			sourceParameters.put(key, value.toString());
	   			sourceParameters.put(KEY_F0, String.valueOf(((double)(1.0)) / Double.valueOf(value[0])));
	   			//this.sourceParameters[key] = value;
	   			//this.sourceParameters[this.KEY_F0] =[str(float(1.0) / float(value[0]))];
	   		}
	   		else {
	
	            // This error will only occur if period is 
	            // zero - which it shouldn't be.
	    		sourceParameters.put(key, "1.0");
	    		sourceParameters.put(KEY_F0, "1.0");
	            //this.sourceParameters[key] = ['1.0'];
	            //this.sourceParameters[this.KEY_F0] = ['1.0'];
	    	}
	    	
	    }
	    else if(key.equals(KEY_F0)) {  // F0 is the frequency in Hz.
	    	// Here period is automatically computed from 
	    	// the frequency.
			if(Double.valueOf(value[0]) == 0){
				sourceParameters.put(key, value.toString());
				sourceParameters.put(KEY_P0, String.valueOf(((double)(1.0)) / Double.valueOf(value[0])));
	//        			this.sourceParameters[key] = value;
	//        			this.sourceParameters[this.KEY_P0] =[str(float(1.0) / float(value[0]))];
			}
			else
			{
				// This error will only occur if frequency 
				// is zero - which it shouldn't be.
				sourceParameters.put(key, "1.0");
				sourceParameters.put(KEY_F0, "1.0");
	//        			this.sourceParameters[key] = ['1.0'];
	//        			this.sourceParameters[this.KEY_F0] = ['1.0'];
			}
		}
	    else if(key.equals(this.KEY_ELONG)) {  // Ecliptic longitude
	    	sourceParameters.put(key, value.toString());
	    }
        
	    else if(key.equals(this.KEY_ELAT))   // Ecliptic latitude
	    {
	    	sourceParameters.put(key, value.toString());
	    }
	    else
	    {
	        // No matter what, we add any other parameter 
	        // we find to the parameters dictionary
	    	sourceParameters.put(key, value.toString());
	    }
	    // Check the coordinates stored are correct, and 
	    // update them as appropriate.
	    String ra = this.get_parameter(this.KEY_RAJ);
	    String dec = this.get_parameter(this.KEY_DECJ);
	    String elong = this.get_parameter(this.KEY_ELONG);
	    String elat = this.get_parameter(this.KEY_ELAT);
	
	    // If no RA or DEC are supplied, then elong and 
	    // elat must have been provided instead. This is 
	    // due to the nature of the ATNF catalog file (this
	    // is empirically observed to be the case).
	    if (ra == null && dec == null && elong != null && elat != null) {
	        String[] corrected_coords = this.checkCoords("00:00:00","00:00:00", elong, elat);
	        // corrected_coords = [ra, dec, elong, elat]
	        sourceParameters.put(KEY_RAJ, corrected_coords[0]);
	        sourceParameters.put(KEY_DECJ, corrected_coords[1]);
	//            this.sourceParameters[this.KEY_RAJ] = [corrected_coords[0]]
	//            this.sourceParameters[this.KEY_DECJ] = [corrected_coords[1]]
	    }
	    // Return true, assuming there have been no errors.
	    // It would be better to check that values have been 
	    // correctly set in the parameters dictionary, but I 
	    // don't currently have the time to implement such 
	    // detailed checks.
	    return true;
	}
    // ******************************************************

    
    public double[] equatorialToGalactic(double ascension, double declination){
    	
    	double b,l;
    	double ascensionR = Math.toRadians(ascension);
    	double declinationR = Math.toRadians(declination);
    	double tmp274 = Math.toRadians(27.4);
    	double tmp192 = Math.toRadians(192.25); 
    	b=Math.pow(Math.sin( Math.cos(declinationR) * Math.cos(tmp274) * Math.cos(ascensionR - tmp192) + Math.sin(declinationR) * Math.asin(tmp274) ), -1);
    	double x = Math.asin(declinationR)-Math.sin(b)*Math.sin(tmp274);
    	double y = Math.cos(declinationR)*Math.cos(tmp274)*Math.sin(ascensionR-tmp192);
    	l=Math.pow( Math.tan( x / y ) ,-1) + 33;
   
    	if(x<0 && y>0)
    		l= l + 180;
    	if(x<0 && y<0)
    		l=l - 360;
    	if(x>0 && y<0)
    		l=l + 360;
    	
    	double[] result = new double[] {b,l};

    	return result;
    	
    }
    
    public String[] deg2HMSandDMS(double ra ,double dec) {
    	
    	double decD = Math.floor(dec);
    	double decM = Math.floor((dec-decD)*60);
    	double decS = (((dec-decD)*60)-decM)*60;
    	decS = Math.round(decS * 100.0) / 100.0;
    	
    	
    	double raH = Math.floor(ra/15);
    	double raM = Math.floor(((ra/15)-raH)*60);
    	double raS = ((((ra/15)-raH)*60)-raM)*60;
    	raS = Math.round(raS * 100.0) / 100.0;
    	
    	String[] result= new String[] {};

    	result[0] = String.valueOf((int)raH);
    	result[1] = String.valueOf((int)raM);
    	result[2] = String.valueOf(raS);
    	
    	result[3] = String.valueOf((int)decD);
    	result[4] = String.valueOf((int)decM);
    	result[5] = String.valueOf(decS);
    	
    	return result;
    }
    
    public double[] hMSandDMS2Deg(String RA, String DEC) {
    	String[] RA_COMPS = RA.split(":");
        String[] DEC_COMPS = DEC.split(":");
        
        double raDeg = Double.parseDouble(RA_COMPS[0])*15 + 
        		Double.parseDouble(RA_COMPS[1])/4 + 
        		Double.parseDouble(RA_COMPS[1])/240;
        
        double decDeg = Double.parseDouble(DEC_COMPS[0]) + 
        		Double.parseDouble(DEC_COMPS[1])/60 + 
        		Double.parseDouble(DEC_COMPS[1])/3600;

       return new double[] {raDeg,decDeg};
    }
    
    
    public  String[] checkCoords(String RA, String DEC, String EL, String EB) {
        /*
        Checks that RA, DEC, GL and GB coordinates are 
        non-empty. Some ATNF entries have no RAJ or DECJ 
        listed, only Equatorial longitude and latitude. 
        Likewise some candidates have RAJ and DECJ listed, 
        but no galactic coordinates.

        This function computes the RAJ and DECJ using ELong 
        and ELat, if the RAJ or DECJ are missing. Likewise 
        it computes the galactic longitude and latitude 
        using the RAJ and DECJ, if longitude or latitude are
        missing.
        
        Parameters
        ----------

        RA  : string
            The right ascension as a string.
        DEC : string
            The declination as a string.
        EL  : string
            The equatorial longitude as a string.
        EB  : string
            The equatorial latitude as a string.

        Returns
        -------
        list of strings
            A list containing RA, DEC, EL and EB in 
            that order.

        Examples
        --------

        >>> EL = "108.172"
        >>> EB = "-42.985"
        >>> [RA,DEC,EL,EB] = checkCoords("0","0",EL,EB)
        >>> print RA
        00:06:04.8
        >>> print DEC
        +18:34:59
        >>> print EL
        108.172
        >>> RA = "12:40:17.61"
        >>> DEC = "-41:24:51.7"
        >>> [RA,DEC,EL,EB] = checkCoords(RA,DEC,"0","0")
        >>> print EL
        300.688
        >>> print EB
        21.4088

        */
    	
    	CalendarAstronomer calendarAstronomer = null;
    	
        if (RA.equals("00:00:00") && DEC.equals("00:00:00")) 

            // No RA and DEC provided. Try to create from
            // EL and EB
            if (EL.equals("0") && EB.equals("0")) {

                // Here just return the inputs, since we 
                // can't convert...
                return  new String[] {RA, DEC, EL, EB};
            }
            else {
                // to Equatorial coordinates...

            	Equatorial ec = calendarAstronomer.eclipticToEquatorial(new Ecliptic(Double.parseDouble(EL),Double.parseDouble(EB)));
            	
            	RA =String.valueOf(ec.ascension);
            	DEC =String.valueOf(ec.declination);
            	
            	String[] raDec = deg2HMSandDMS(ec.ascension, ec.declination);

                // Now get galactic coordinates.
            	double[] gal =equatorialToGalactic(ec.ascension, ec.declination);
                String GB = String.valueOf(gal[0]);
                String GL = String.valueOf(gal[1]);
                //GL, GB = str(SkyCoord(coordinateString).galactic.to_string()).split();

                return new String[] {RA, DEC, GL, GB};
            }
        
        if(EL.equals("0") && EB.equals("0")) {

            // No EL and EB provided.
            if(RA.equals("00:00:00") && DEC.equals("00:00:00")){

                // Here just return the inputs, since we 
                // can't convert...
                return new String[] {RA, DEC, EL, EB};
            }

            else {
            	
            	double[] raDec = hMSandDMS2Deg(RA, DEC);
                
            	double[] gal =equatorialToGalactic(raDec[0], raDec[1]);
                String GB = String.valueOf(gal[0]);
                String GL = String.valueOf(gal[1]);

                
                return new String[] {RA, DEC, EL, EB};
            }
        }

        return new String[] {RA, DEC, EL, EB};
    }
    // *****************************************************

    public String get_parameter(String key) {
        /*
        Attempts to retrieve a parameter from the 
        sourceParameters dictionary. If the parameter is 
        in the dictionary it is returned, else the value
        None is returned instead.

        Detail
        ----------
        The data items which belong to a PSRCATEntry 
        object, are stored in the sourceParameters 
        dictionary. Each entry in the dictionary describes 
        the data stored in a single line of an ANTF pulsar 
        catalog file. For example, if the file contains the 
        following lines:

        PSRJ     J0006+1834                    cnt96
        RAJ      00:06:04.8               2    cn95
        DECJ     +18:34:59                4    cn95
        P0       0.69374767               14   cn95
        DM       12.0                     6    cn95

        then the dictionary will contain the following entries:

        -----------------------------------------------------
        |   Key     |   Value                               |
        -----------------------------------------------------
        |   'PSRJ'  |   [ 'J0006+1834' , 'cnt96' ]          |
        |   'RAJ'   |   [ '00:06:04.8' , '2'     , 'cn95' ] |
        |   'DECJ'  |   [ '+18:34:59'  , '4'     , 'cn95' ] |
        |   'P0'    |   [ '0.69374767' , '14'    , 'cn95' ] |
        |   'DM'    |   [ '12.0' , '6' , 'cn95' ]           |
        -----------------------------------------------------

        Thus each key is a string, and each value a list of 
        strings. It is possible therefore to obtain a specific 
        value in the string list.

        Parameters
        ----------
        :param key: the key used to retrieve values from 
                    the sourceParameters dictionary.

        Returns
        ----------
        A string literal corresponding to the desired 
        parameter if the key is valid, else None.

        Examples
        --------
        >>> entry = PSRCATEntry('J0006+1834')
        >>> print entry.sourceName
        J0006+1834
        >>> line_from_file = 'DM 12.0 6 cn95'
        >>> entry.process_atnf_formatted_line(line_from_file)
        >>> print entry.get_parameter('DM')
        12.0

        */

        
        	String value = sourceParameters.get(key);
            //value = this.sourceParameters[key];
        	return value;
        		
       
    }
    
    
    // ******************************************************

    public double getRefSep() {
        /*
        Computes the angular separation between this 
        PSRCATEntry object, and a reference point at 
        (RA=00:00:00,DEC=00:00:00). Returns the separation
        as a floating point value.

        Parameters
        ----------

        N/A

        Returns
        -------
        A floating point value.

        Examples
        --------

        >>> entry = PSRCATEntry('J0002+0002')
        >>> print entry.sourceName
        J0002+0002
        >>> RA_line = 'RAJ 00:10:00 2'
        >>> DEC_line = 'DECJ +00:00:00 8'
        >>> entry.process_atnf_formatted_line(RA_line)
        >>> entry.process_atnf_formatted_line(DEC_line)
        >>> print entry.getRefSep()
        2.5
        */
    	double[] raDec = hMSandDMS2Deg(sourceParameters.get(KEY_RAJ), sourceParameters.get(KEY_DECJ));
    	
    	double[] raDecR = new double[2];
    	raDecR[0] = Math.toRadians(raDec[0]);
    	raDecR[1] = Math.toRadians(raDec[1]);
    	
    	
    	double x = Math.sqrt( Math.pow(Math.cos(0),2) * Math.pow(Math.sin(0 - raDecR[0]),2) + 
    			Math.pow(Math.cos(raDecR[1])*Math.sin(0) - Math.sin(raDecR[1])*Math.cos(0)*Math.cos(0 - raDecR[0]),2));
    	
    	double y = Math.sin(raDecR[1])*Math.sin(0) + Math.cos(raDecR[1])*Math.cos(0)*Math.cos(0 - raDecR[0]);
    	
    	double tan = Math.pow(Math.tan(x/y), -1);
    	
    	if(tan<0) {
    		this.refsep = (180/Math.PI)*tan + 180;
    	}else
    	{
    		this.refsep = (180/Math.PI)*tan;
    	}
    		
        return refsep;
    }
    
    
    // ******************************************************

    public double calcsep(String[] coord) {
        /*
        Computes the angular separation between this 
        PSRCATEntry object, and a reference point described 
        by the coord object (from Astropy).

        Parameters
        ----------

        :param coord: the coordinate to compute the 
                      distance to.

        Returns
        -------
        A floating point value describing the angular 
        separation.

        Examples
        --------

        >>> entry = PSRCATEntry('J0002+0002')
        >>> print entry.sourceName
        J0002+0002
        >>> RA_line  = 'RAJ 00:10:00 2'
        >>> DEC_line = 'DECJ +00:00:00 8'
        >>> entry.process_atnf_formatted_line(RA_line)
        >>> entry.process_atnf_formatted_line(DEC_line)
        >>> print entry.calcsep(coord) 
        2.5
        */

        if (coord != null){

            if(this.coord != null) {
            	
            	double[] raDec = hMSandDMS2Deg(sourceParameters.get(KEY_RAJ), sourceParameters.get(KEY_DECJ));
            	double[] raDecR = new double[] {};
            	raDecR[0] = Math.toRadians(raDec[0]);
            	raDecR[1] = Math.toRadians(raDec[1]);
            	
            	double[] coordRaDec = hMSandDMS2Deg(coord[0], coord[1]);
            	double[] coordRaDecR = new double[] {};
            	coordRaDecR[0] = Math.toRadians(coordRaDec[0]);
            	coordRaDecR[1] = Math.toRadians(coordRaDec[1]);
            	
            	
            	double x = Math.sqrt( Math.pow(Math.cos(coordRaDecR[1]),2) * Math.pow(Math.sin(coordRaDecR[0] - raDecR[0]),2) + 
            			Math.pow(Math.cos(raDecR[1])*Math.sin(coordRaDecR[1]) - Math.sin(raDecR[1])*Math.cos(coordRaDecR[1])*Math.cos(coordRaDecR[0] - raDecR[0]),2));
            	
            	double y = Math.sin(raDecR[1])*Math.sin(coordRaDecR[1]) + Math.cos(raDecR[1])*Math.cos(coordRaDecR[1])*Math.cos(coordRaDecR[0] - raDecR[0]);
            	
            	double tan = Math.pow(Math.tan(x/y), -1);
            	double sep;
            	if(tan<0) {
            		sep = (180/Math.PI)*tan + 180;
            	}else
            	{
            		sep = (180/Math.PI)*tan;
            	}
            	
                return sep;
            }
            else
                // Return a large separation if it 
                // cannot be computed.
                return 100000; 
        }
        else
            // Return a large separation if it cannot 
            // be computed.
            return 100000;
    }

    // ******************************************************

    
    public String __str__() {
        /*
        Overridden method that provides a neater string 
        representation of this class. This is useful when 
        writing these objects to a file or the terminal.

        Parameters
        ----------
        N/A

        Returns
        ----------
        :return: a string representation of this class 
                in comma separated value (CSV) format.

        Examples
        --------
        >>> e = PSRCATEntry('J0006+1834')
        >>> print entry.sourceName
        J0006+1834
        >>> e.process_atnf_formatted_line('RAJ 00:06:04.8 2')
        >>> e.process_atnf_formatted_line('DECJ +18:34:59 4')
        >>> e.process_atnf_formatted_line('P0 0.6937476 14 ')
        >>> e.process_atnf_formatted_line('DM 12.0 6')
        >>> print str(e)
        J0006+1834,00:06:04.8,+18:34:59,0.6937476,12.0
        */

        // Extract the key parameters.
        String raj = this.get_parameter("RAJ");
        String decj = this.get_parameter("DECJ");
        String p0 = this.get_parameter("P0");
        String dm = this.get_parameter("DM");

        return this.sourceName + "," + raj + "," +
               decj + "," + p0 +  "," + dm +
               ',' + this.refsep;
    }
    
    public String getName() {
    	return sourceName;
    }
    
    public void setRefSep(double _refSep) {
    	refsep = _refSep;
    }
        
        // ***************************************************
    
    
    
}
    
    