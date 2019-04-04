package r01fb.test.rx;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reactivestreams.Subscriber;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.HttpClient;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.util.types.collections.Lists;

@Slf4j
public class RXRandomNumberGenerator {
/////////////////////////////////////////////////////////////////////////////////////////
//  RANDOM NUMBER GEN SERVICE
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String RANDOM_NUMBER_GEN_WEB_SERVICE_URL_TEMPLATE = "http://qrng.anu.edu.au/form_handler.php?numofsets=1&min_num={}&max_num={}&repeats=no&num_per_set=1";
	private static final Pattern RANDOM_NUMBER_GEN_WEB_SERVICE_RESPNOSE_PATTERN = Pattern.compile("<br />Set 1: ([0-9]+)<br />");	// Generated Random permutations without repetitions<br/>Your random numbers are: <br />Set 1: 25<br />
/////////////////////////////////////////////////////////////////////////////////////////
//  main
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(final String[] args) {
		// Randomize all the spectrum
//		List<Integer> randoms = RXRandomNumberGenerator.generateRandomNumbers(Range.closed(1,50));
		List<Integer> randoms = Lists.newArrayList(45, 17, 26, 21, 28, 35, 33, 9, 43, 16, 14, 25, 36, 11, 7, 46, 2, 37, 4, 19, 27, 40, 48, 20, 3, 30, 47, 31, 18, 29, 41, 50, 42, 34, 24, 15, 10, 23, 13, 6, 1, 32, 49, 5, 39, 8, 38, 12, 44, 224);

		// generate numbers
//		_playPrimitiva(randoms,
//					   1);			// number of picks
		_playEuromillon(randoms,
						1);			// number of picks
		

	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static List<Integer> _generateRandomNumbers(final int number,
													    final Range<Integer> range) {
		final Url randomNumGeneratorWebServiceUrl = Url.from(Strings.customized(RANDOM_NUMBER_GEN_WEB_SERVICE_URL_TEMPLATE,
																				 range.lowerEndpoint(),range.upperEndpoint()));
		log.info("> Random number generator service url: {}",randomNumGeneratorWebServiceUrl);

		// generate the given number of random numbers
		final Set<Integer> outSet = Sets.newLinkedHashSetWithExpectedSize(number);
		int numberOfRandomsToGenerate = number;
		log.info("...generating {} random numbers",numberOfRandomsToGenerate);
		do {
			Flowable<Integer> flowable = Flowable.fromCallable(new Callable<Integer>() {
																		@Override
																		public Integer call() throws Exception {
																			return _genrateRandomNumber(randomNumGeneratorWebServiceUrl);
																		}
															   })		// generates just a single random number
													.repeat(numberOfRandomsToGenerate);	// ... so repeat
			Collection<Integer> randomNums = flowable.toList().blockingGet();	// flowable to list

			outSet.addAll(randomNums);
			numberOfRandomsToGenerate = number - outSet.size();
		} while(numberOfRandomsToGenerate > 0);
		return Lists.newArrayList(outSet);
	}
	private static final int _genrateRandomNumber(final Url randomNumGeneratorWebServiceUrl) throws IOException {
		String response = HttpClient.forUrl(randomNumGeneratorWebServiceUrl)
									.GET()
									.loadAsString()
									.directNoAuthConnected();
		log.trace("\t-Random number generator web service response {}",response);

		// The service response is something like:
		//	  -Generated Random permutations without repetitions<br/>Your random numbers are: <br />Set 1: 25<br />
		Matcher m = RANDOM_NUMBER_GEN_WEB_SERVICE_RESPNOSE_PATTERN.matcher(response);
		if (m.find()) {
			String randomNumStr = m.group(1);
			int outNum = Integer.parseInt(randomNumStr);
			System.out.print(Strings.customized("{} ",outNum));
			return outNum;
		} else {
			log.error("The random number generator web service response is NOT valid: {}",response);
			return -1;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static List<Integer> generateRandomNumbers(final Range<Integer> range) {

		List<Integer> outNumbers = _generateRandomNumbers(range.upperEndpoint() - range.lowerEndpoint() + 1,	// SPECTRUM_SIZE numbers
												  		  range);	// within the given range
		log.info("...suffling the {} generated numbers",outNumbers.size());

		log.info("> Generated spectrum: {}",outNumbers);
		return outNumbers;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN
/////////////////////////////////////////////////////////////////////////////////////////
	private static int _pickNumberWithin(final List<Integer> randoms,
										 final Range<Integer> range) {
		int out = -1;
		do {
			int pickPos = new SecureRandom(UUID.randomUUID().toString().getBytes())
									.nextInt(randoms.size()-1);
			Integer randomNum = randoms.get(pickPos);
			if (range.contains(randomNum)) out = randomNum;
		} while(out < 0);
		return out;
	}
	private static void _play(final List<Integer> randoms,
							  final Range<Integer> mainRange,final int numOfMainNumbers,
							  final Range<Integer> auxRange,final int numOfAuxNumbers,
							  final int numberOfPicks) {
		for (int i=1; i <= numberOfPicks; i++) {
			System.out.println("Pick " + i + "=================================================");
			// Main picks
			if (mainRange != null) {
				Set<Integer> r1 = Sets.newLinkedHashSet();
				do {
					Integer randomNum = _pickNumberWithin(randoms,
														  mainRange);
					if (!r1.contains(randomNum)) r1.add(randomNum);
				} while (r1.size() < numOfMainNumbers);
				List<Integer> mains = Lists.newArrayList(r1);
				Collections.sort(mains);
				log.info(">Main: {}",mains);
				System.out.println("=====>Main: " + mains);
			}
	
			// Aux picks
			if (auxRange != null) {
				Set<Integer> r2 = Sets.newLinkedHashSet();
				do {
					Integer randomNum = _pickNumberWithin(randoms,
														  auxRange);
					if (!r2.contains(randomNum)) r2.add(randomNum);
				} while (r2.size() < numOfAuxNumbers);
				List<Integer> aux = Lists.newArrayList(r2);
				Collections.sort(aux);
				log.info(">Aux: {}",aux);
				System.out.println("=====>Aux: " + aux);
			}
		}
	}
	private static void _playPrimitiva(final List<Integer> randoms,
									   final int numberOfPicks) {
		log.info("PLAYING LA PRIMITIVA (RxJava TEST)");
		_play(randoms,
			  Range.closed(1,49),6,
			  null,0,
			  numberOfPicks);
	}
	private static void _playEuromillon(final List<Integer> randoms,
										final int numberOfPicks) {
		log.info("PLAYING EUROMILION (RxJava TEST)");
		_play(randoms,
			  Range.closed(1,50),5,
			  Range.closed(1,12),2,
			  numberOfPicks);
	}
}
