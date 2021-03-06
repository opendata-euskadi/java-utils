package r01f.api.interfaces.s3.impl;

import com.amazonaws.services.s3.AmazonS3;

abstract class S3ServiceBaseImpl {
///////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
	 protected final AmazonS3 _s3Client;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public S3ServiceBaseImpl(final AmazonS3 s3Client) {
		_s3Client = s3Client;
	}
}
