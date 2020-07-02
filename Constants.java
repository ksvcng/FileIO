package main;

public class Constants {

	public static final String GrepQuotationContent = "(?<=\").*(?=\")";
	public static final String GrepQuotationContentFromValueMap = ".*(?=_[0-9]+)";
	public static final String AddMemberDataMap = "addmember DataMap,";
	public static final String AddMemberValueMap = "addmember ValueMap,";
	public static final String DataMap = "DataMap:";
	public static final String ValueMap = "ValueMap:";
	public static final String FilterDataMapComponent = "^(DataMap:\").*?(\")";
	public static final String FilterValueMapComponent = "^(ValueMap:\").*?(\")";
	public static final String NewLine = "\n";
	
}
