package r01f.filestore.api;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.DirectoryWalker;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.collections.FakeCollection;
import r01f.types.Path;

@Slf4j
@RequiredArgsConstructor
public class FilesObservable {
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static Flowable<Path> createFilesObservable(final Path rootFolder) {
		Flowable<Path> out = Flowable.create(new FlowableOnSubscribe<Path>() {
														@Override
														public void subscribe(final FlowableEmitter<Path> emitter) throws Exception {
															final FileWalker fileWalker = new FileWalker(emitter);
															fileWalker.startWalking(rootFolder);
															emitter.onComplete();
														}
											 },
										     BackpressureStrategy.BUFFER);
		return out;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		Flowable<Path> flowable = FilesObservable.createFilesObservable(Path.from("d:/temp_dev"));
		flowable.subscribe(new Subscriber<Path>() {
									@Override
									public void onSubscribe(final Subscription s) {
										System.out.println("====>SUBSCRIBED!!");
										s.request(Long.MAX_VALUE);		// beware!! issue a request, otherwise NO data will be emmited
									}
									@Override
									public void onNext(final Path t) {
										System.out.println("==============>" + t);
									}
									@Override
									public void onError(final Throwable t) {
									}
									@Override
									public void onComplete() {
										System.out.println("====>COMPLETE!!");
									}
						   });
		
	}	
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	private static class FileWalker 
		  	     extends DirectoryWalker<File> {
		
		private final FlowableEmitter<Path> _emitter;
		
	    @Override
	    protected void handleFile(final File file,final int depth,final Collection<File> results) throws IOException {
	        log.info("Found {}: {}",
	        		 file.isDirectory() ? "dir" : "file",
	        		 file.getAbsolutePath());
	        // emit
	        _emitter.onNext(Path.from(file));
	    }
	    public void startWalking(final Path rootPath) throws IOException {
	    	File rootDir = new File(rootPath.asAbsoluteString());
	    	this.walk(rootDir,
	    			  // a collection that just DOES NOT STORE ANYTHING
	    			  new FakeCollection<File>());
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////

}
