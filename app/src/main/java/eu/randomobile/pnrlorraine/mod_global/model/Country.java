package eu.randomobile.pnrlorraine.mod_global.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class Country {
	private String iso;
    private String code;
    private String name;
    
    public String getIso() {
		return iso;
	}


	public void setIso(String iso) {
		this.iso = iso;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}




	

    public Country(String iso, String code, String name) {
        this.iso = iso;
        this.code = code;
        this.name = name;
    }

    
    public static ArrayList<Country> getListCountries(){
    	//
        // A collection to store our country object
        //
        ArrayList<Country> countries = new ArrayList<Country>();

        //
        // Get ISO countries, create Country object and
        // store in the collection.
        //
        String[] isoCountries = Locale.getISOCountries();
        for (String country : isoCountries) {
            Locale locale = new Locale("en", country);
            String iso = locale.getISO3Country();
            String code = locale.getCountry();
            String name = locale.getDisplayCountry();

            if (!"".equals(iso) && !"".equals(code)
                    && !"".equals(name)) {
                countries.add(new Country(iso, code, name));
            }
        }

        //
        // Sort the country by their name and then display the content
        // of countries collection object.
        //
        Collections.sort(countries, new CountryComparator());
        return countries;
    }
    
    // Returns the lang code for the current device
    public static String getDeviceCurrentCountry(){
    	Locale localeActual = Locale.getDefault();
        String lang = localeActual.getLanguage();
        return lang;
    }
    
    
    public String toString() {
        //return iso + " - " + code + " - " + name.toUpperCase();
    	return name;
    }
    
    
    
    
    /**
     * CountryComparator class.
     */
    static class CountryComparator implements Comparator<Country> {
        @SuppressWarnings("rawtypes")
		private Comparator comparator;

        CountryComparator() {
            comparator = Collator.getInstance();
        }

        @SuppressWarnings("unchecked")
        public int compare(Country c1, Country c2) {
            return comparator.compare(c1.name, c2.name);
        }
    }
}
