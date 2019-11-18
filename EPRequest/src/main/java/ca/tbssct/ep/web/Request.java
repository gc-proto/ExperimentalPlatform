package ca.tbssct.ep.web;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Request {

	private String domainNamePrefix;
	private String emailAddress;
	private String endDate;
	private String experimentName;
	private String experimentDesc;
	private String department;
	private String yourName;
	
	public char charAt(int index) {
		return yourName.charAt(index);
	}
	public IntStream chars() {
		return yourName.chars();
	}
	public int codePointAt(int index) {
		return yourName.codePointAt(index);
	}
	public int codePointBefore(int index) {
		return yourName.codePointBefore(index);
	}
	public int codePointCount(int beginIndex, int endIndex) {
		return yourName.codePointCount(beginIndex, endIndex);
	}
	public IntStream codePoints() {
		return yourName.codePoints();
	}
	public int compareTo(String anotherString) {
		return yourName.compareTo(anotherString);
	}
	public int compareToIgnoreCase(String str) {
		return yourName.compareToIgnoreCase(str);
	}
	public String concat(String arg0) {
		return yourName.concat(arg0);
	}
	public boolean contains(CharSequence s) {
		return yourName.contains(s);
	}
	public boolean contentEquals(CharSequence arg0) {
		return yourName.contentEquals(arg0);
	}
	public boolean contentEquals(StringBuffer sb) {
		return yourName.contentEquals(sb);
	}
	public boolean endsWith(String suffix) {
		return yourName.endsWith(suffix);
	}
	public boolean equals(Object arg0) {
		return yourName.equals(arg0);
	}
	public boolean equalsIgnoreCase(String anotherString) {
		return yourName.equalsIgnoreCase(anotherString);
	}
	public byte[] getBytes() {
		return yourName.getBytes();
	}
	public byte[] getBytes(Charset charset) {
		return yourName.getBytes(charset);
	}
	public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) {
		yourName.getBytes(srcBegin, srcEnd, dst, dstBegin);
	}
	public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
		return yourName.getBytes(charsetName);
	}
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		yourName.getChars(srcBegin, srcEnd, dst, dstBegin);
	}
	public int hashCode() {
		return yourName.hashCode();
	}
	public int indexOf(int ch, int fromIndex) {
		return yourName.indexOf(ch, fromIndex);
	}
	public int indexOf(int ch) {
		return yourName.indexOf(ch);
	}
	public int indexOf(String str, int fromIndex) {
		return yourName.indexOf(str, fromIndex);
	}
	public int indexOf(String str) {
		return yourName.indexOf(str);
	}
	public String intern() {
		return yourName.intern();
	}
	public boolean isBlank() {
		return yourName.isBlank();
	}
	public boolean isEmpty() {
		return yourName.isEmpty();
	}
	public int lastIndexOf(int ch, int fromIndex) {
		return yourName.lastIndexOf(ch, fromIndex);
	}
	public int lastIndexOf(int ch) {
		return yourName.lastIndexOf(ch);
	}
	public int lastIndexOf(String str, int fromIndex) {
		return yourName.lastIndexOf(str, fromIndex);
	}
	public int lastIndexOf(String str) {
		return yourName.lastIndexOf(str);
	}
	public int length() {
		return yourName.length();
	}
	public Stream<String> lines() {
		return yourName.lines();
	}
	public boolean matches(String regex) {
		return yourName.matches(regex);
	}
	public int offsetByCodePoints(int index, int codePointOffset) {
		return yourName.offsetByCodePoints(index, codePointOffset);
	}
	public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
		return yourName.regionMatches(ignoreCase, toffset, other, ooffset, len);
	}
	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		return yourName.regionMatches(toffset, other, ooffset, len);
	}
	public String repeat(int arg0) {
		return yourName.repeat(arg0);
	}
	public String replace(char arg0, char arg1) {
		return yourName.replace(arg0, arg1);
	}
	public String replace(CharSequence target, CharSequence replacement) {
		return yourName.replace(target, replacement);
	}
	public String replaceAll(String regex, String replacement) {
		return yourName.replaceAll(regex, replacement);
	}
	public String replaceFirst(String regex, String replacement) {
		return yourName.replaceFirst(regex, replacement);
	}
	public String[] split(String arg0, int arg1) {
		return yourName.split(arg0, arg1);
	}
	public String[] split(String regex) {
		return yourName.split(regex);
	}
	public boolean startsWith(String arg0, int arg1) {
		return yourName.startsWith(arg0, arg1);
	}
	public boolean startsWith(String prefix) {
		return yourName.startsWith(prefix);
	}
	public String strip() {
		return yourName.strip();
	}
	public String stripLeading() {
		return yourName.stripLeading();
	}
	public String stripTrailing() {
		return yourName.stripTrailing();
	}
	public CharSequence subSequence(int beginIndex, int endIndex) {
		return yourName.subSequence(beginIndex, endIndex);
	}
	public String substring(int beginIndex, int endIndex) {
		return yourName.substring(beginIndex, endIndex);
	}
	public String substring(int beginIndex) {
		return yourName.substring(beginIndex);
	}
	public char[] toCharArray() {
		return yourName.toCharArray();
	}
	public String toLowerCase() {
		return yourName.toLowerCase();
	}
	public String toLowerCase(Locale locale) {
		return yourName.toLowerCase(locale);
	}
	public String toString() {
		return yourName.toString();
	}
	public String toUpperCase() {
		return yourName.toUpperCase();
	}
	public String toUpperCase(Locale locale) {
		return yourName.toUpperCase(locale);
	}
	public String trim() {
		return yourName.trim();
	}
	public String getDomainNamePrefix() {
		return domainNamePrefix;
	}
	public void setDomainNamePrefix(String domainNamePrefix) {
		this.domainNamePrefix = domainNamePrefix;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getExperimentName() {
		return experimentName;
	}
	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}
	public String getExperimentDesc() {
		return experimentDesc;
	}
	public void setExperimentDesc(String experimentDesc) {
		this.experimentDesc = experimentDesc;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	

}
