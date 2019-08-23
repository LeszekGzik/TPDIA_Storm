package main;

import java.util.HashMap;
import java.util.Map;

//public class PSRCATEntry {
//
//}


//
//***********************************************************
//
// PSRCATEntry.py
//
//***********************************************************
// Description:
//
// Represents an individual entry in the ATNF Pulsar catalog.
//
// Requires the pyephem and astropy modules.
//
//***********************************************************
// Author: Rob Lyon
// Email : robert.lyon@manchester.ac.uk
// web   : www.scienceguyrob.com
//***********************************************************
// License:
//
// Code made available under the GPLv3 (GNU General Public
// License), that allows you to copy, modify and redistribute
// the code as you see fit:
//
// http://www.gnu.org/copyleft/gpl.html
//
// Though a mention to the original author using the citation
// above in derivative works, would be very much appreciated.
//************************************************************

// For coordinate transformations.
//import com.ibm.icu.impl.*;
//import ephem;

import com.ibm.icu.impl.CalendarAstronomer;
import org.python.util.PythonInterpreter;


//from astropy.coordinates import SkyCoord
//import SkyCoord;

// ******************************
//
// CLASS DEFINITION
//
// ******************************

public class PSRCATEntry {

	
	//String[] sourceParameters;
	static PythonInterpreter interp = new PythonInterpreter();
	Map<String, String> sourceParameters= new HashMap<String, String>();
    String sourceName;
    String JName;
    String BName;
    double refsep; 
    String coord;

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

    	interp.exec("import ephem");
    	interp.exec("from astropy.coordinates import SkyCoord");
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
        String[] value = {};
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
        else {
        	if(key.equals(this.KEY_PSRB)) {
       		 	this.sourceName = value[0];
       		 	this.BName = value[0];
        	}
        }
        
        // If the text contains the right ascension (RA).
        if (key.equals(this.KEY_RAJ)) {

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
                    sourceParameters.put(key, value.toString());
                    //this.sourceParameters[key] = value;
                }
                else {
                	if(length == 2) {
                		raj += ":00";  // Add ss parts.
                        value[0] = raj;
                        sourceParameters.put(key, value.toString());
                        //this.sourceParameters[key] = value;
                	}
                }
            }       
            else {
            	sourceParameters.put(key, value.toString());
            	//this.sourceParameters[key] = value;
            }
                
        // If the text contains declination (DEC).
        }
        else {
        	if(key.equals(this.KEY_DECJ)) {
        		
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
                    sourceParameters.put(key, value.toString());
                    //this.sourceParameters[key] = value;

                }
                else
                	if (length == 2) {
                		decj += ":00";  // Add ss parts.
                		value[0] = decj;
                		sourceParameters.put(key, value.toString());
                		//this.sourceParameters[key] = value;
                	}
            }
            else
            {
            	sourceParameters.put(key, value.toString());
                //this.sourceParameters[key] = value;
            }	
            
        	}
        }
        // P0 is the period in seconds.
       	if(key.equals(this.KEY_P0)) {

       		// Here frequency is automatically computed from
       		// the period.

       		if(Double.valueOf(value[0]) == 0)
       		{
       			sourceParameters.put(key, value.toString());
       			sourceParameters.put(KEY_F0, String.valueOf(((double)(1.0)) / Double.valueOf(value[0])));
       			//this.sourceParameters[key] = value;
       			//this.sourceParameters[this.KEY_F0] =[str(float(1.0) / float(value[0]))];
       		}else
        	{

                // This error will only occur if period is 
                // zero - which it shouldn't be.
        		sourceParameters.put(key, "1.0");
        		sourceParameters.put(KEY_F0, "1.0");
                //this.sourceParameters[key] = ['1.0'];
                //this.sourceParameters[this.KEY_F0] = ['1.0'];
        	}
        	
        }
        else
        {
        	if(key.equals(KEY_F0)) {  // F0 is the frequency in Hz.
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
        }
       
        if(key.equals(this.KEY_ELONG)) {  // Ecliptic longitude
        	sourceParameters.put(key, value.toString());
        }
        
        if(key.equals(this.KEY_ELAT))   // Ecliptic latitude
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
    	
    	CalendarAstronomer calendarAstronomer;
    	
        if (RA.equals("00:00:00") && DEC.equals("00:00:00")) 

            // No RA and DEC provided. Try to create from
            // EL and EB
            if (EL.equals("0") && EB.equals("0")) {

                // Here just return the inputs, since we 
                // can't convert...
                return  new String[] {RA, DEC, EL, EB};
            }
            else {

                // Use pyephem to convert from ecliptic 
                // to Equatorial coordinates...
            	
            	
            	
            	//ec = calendarAstronomer.eclipticToEquatorial(new Ecliptic(EB, EL));
            	//new Ecliptic(lat, lon)
//            	gal = calendarAstronomer.equa;
//                Equatorial eq;
//            	eq.
//            	Ecliptic ecli;
//            	ecli.
            	//ec = atan2((cos))
            	interp.set("EL", EL);
            	interp.set("EB", EB);
            	interp.exec("ec = ephem.Ecliptic(EL, EB, epoch='2000')");
            	interp.exec("RA = str(ec.to_radec()[0])");
            	interp.exec(" DEC = str(ec.to_radec()[1])");
            	
            	RA = interp.get("RA").asString();
            	DEC = interp.get("DEC").asString();
                //ec = ephem.Ecliptic(EL, EB, epoch='2000'); 
                //RA = str(ec.to_radec()[0]);
                //DEC = str(ec.to_radec()[1]);

                // Since we can't just convert from RA and 
                // DEC, to GL and GB in pyephem, we instead 
                // use astropy to do the job. This requires
                // that we first do some daft parsing of the 
                // string into pieces, then reform it in to 
                // the format required by astropy...
                String[] RA_COMPS = RA.split(":");
                String[] DEC_COMPS = DEC.split(":");

                // Now reform the text into astropy format...
                String coordinateString = RA_COMPS[0] + "h" +
                                   RA_COMPS[1] + "m" +
                                   RA_COMPS[2] + "s " +
                                   DEC_COMPS[0] + "d" +
                                   DEC_COMPS[1] + "m" +
                                   DEC_COMPS[2] + "s";

                // Now get galactic coordinates.
                interp.set("coordinateStr", coordinateString);
                interp.exec("GL, GB = str(SkyCoord(coordinateString).galactic.to_string()).split()");
                String GL = interp.get("GL").asString();
                String GB = interp.get("GB").asString();
                
                //GL, GB = str(SkyCoord(coordinateString).galactic.to_string()).split();

                return new String[] {RA, DEC, EL, EB};
            }
        
        if(EL.equals("0") && EB.equals("0")) {

            // No EL and EB provided.
            if(RA.equals("00:00:00") && DEC.equals("00:00:00")){

                // Here just return the inputs, since we 
                // can't convert...
                return new String[] {RA, DEC, EL, EB};
            }

            else {
                // Since we can't just convert from RA and 
                // DEC to GL and GB in pyephem, we instead 
                // use astropy to do the job. This requires
                // that we first do some daft parsing of the
                // string into pieces, then reform it in to
                // the format required by astropy...
                String[] RA_COMPS = RA.split(":");
                String[] DEC_COMPS = DEC.split(":");

                // Now reform the text into astropy format...
                String coordinateString = RA_COMPS[0] + "h" + 
                                   RA_COMPS[1] + "m" + 
                                   RA_COMPS[2] + "s " + 
                                   DEC_COMPS[0] + "d" + 
                                   DEC_COMPS[1] + "m" + 
                                   DEC_COMPS[2] + "s";

                // Now get galactic coordinates.
                interp.set("coordinateStr", coordinateString);
                interp.exec("GL, GB = str(SkyCoord(coordinateString).galactic.to_string()).split()");
                String GL = interp.get("GL").asString();
                String GB = interp.get("GB").asString();
                //GL, GB = str(SkyCoord(coordinateString).galactic.to_string()).split();

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
        // Equatorial parameters
        String ra = this.get_parameter(KEY_RAJ);
        String dec = this.get_parameter(KEY_DECJ);

        if (ra != null && dec != null) {
            String[] RAJ_Components = ra.split(":");
            String RAJ = RAJ_Components[0] + 
                  'h' + RAJ_Components[1] + 
                  'm' + RAJ_Components[2] + 's';

            String[] DEC_Components = dec.split(":");
            String DEC = DEC_Components[0] + 
                  'd' + DEC_Components[1] + 
                  'm' + DEC_Components[2] + 's';

            
            interp.set("RAJ", RAJ);
            interp.set("DEC", DEC);
            
            interp.exec("coord = SkyCoord(RAJ, DEC, frame = \"fk5\")");
            interp.exec("ref = SkyCoord(\"0h0m0s\", \"0d0m0s\", frame = \"fk5\")");
            interp.exec("refsep = this.coord.separation(ref).arcsecond / 3600;");
            
            coord = interp.get("coord").asString();
            refsep =  interp.get("refsep").asDouble();
            
            
            

            // Convert to degrees by dividing by 3,600
            //refsep = this.coord.separation(ref).arcsecond / 3600;
        }
        return refsep;
    }
    
    
    // ******************************************************

    public double calcsep(String coord) {
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
            	interp.set("coord", coord);
            	interp.exec("sep = this.coord.separation(coord).arcsecond / 3600;");
                double sep = interp.get("sep").asDouble();
            	
            	//sep = this.coord.separation(coord).arcsecond / 3600;
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
        
        // ***************************************************
    
    
    
}
    
    