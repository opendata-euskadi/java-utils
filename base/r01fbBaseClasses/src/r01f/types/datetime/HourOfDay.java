package r01f.types.datetime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Dates;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="hourOfDay")
@GwtIncompatible
@Accessors(prefix="_")
public class HourOfDay 
  implements Serializable,
  			 CanBeRepresentedAsString,
  			 Comparable<HourOfDay> {

	private static final long serialVersionUID = 8445517567471520680L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private int _hourOfDay;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
   public static boolean canBe(final String str) {
         return Strings.isNOTNullOrEmpty(str) 
             && Numbers.isInteger(str)
             && Integer.parseInt(str) >= 0 && Integer.parseInt(str) < 24;
   }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public HourOfDay(final int hourOfDay) {
		_set(hourOfDay);
	}
	public HourOfDay(final Integer hourOfDay) {
		_set(hourOfDay);
	}
	public HourOfDay(final String month) { 
		int m = Integer.parseInt(month);
		_set(m);
	}
	public static HourOfDay of(final String hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	public static HourOfDay of(final Date date) {
		return new HourOfDay(Dates.asCalendar(date).get(Calendar.HOUR_OF_DAY));
	}
	public static HourOfDay of(final DateTime date) {
		return new HourOfDay(date.getHourOfDay());
	}
	public static HourOfDay of(final LocalTime time) {
		return new HourOfDay(time.getHourOfDay());
	}
	public static HourOfDay of(final int hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	public static HourOfDay valueOf(final String hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	public static HourOfDay fromString(final String hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private void _set(final int hourOfDay) {
		Preconditions.checkArgument(hourOfDay < 24 || hourOfDay >= 0,"Not a valid hour of day");
		_hourOfDay = hourOfDay;		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(_hourOfDay);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public int asInteger() {
		return _hourOfDay;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final HourOfDay other) {
		return _hourOfDay == other.asInteger();
	}
	public boolean isNOT(final HourOfDay other) {
		return !this.is(other);
	}
	public boolean isBefore(final HourOfDay other) {
		return _hourOfDay < other.asInteger();
	}
	public boolean isAfter(final HourOfDay other) {
		return _hourOfDay > other.asInteger();
	}
	public boolean isBeforeOrEqual(final HourOfDay other) {
		return _hourOfDay <= other.asInteger();
	}
	public boolean isAfterOrEqual(final HourOfDay other) {
		return _hourOfDay >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof HourOfDay) return ((HourOfDay)obj).getHourOfDay() == _hourOfDay;
		return false;
	}
	@Override
	public int hashCode() {
		return new Integer(_hourOfDay).hashCode();
	}
	@Override
	public int compareTo(final HourOfDay other) {
		return new Integer(this.asInteger())
						.compareTo(new Integer(other.asInteger()));
	}
}
