PRIVATE_KEY=data/encryptKey.key
PUBLIC_KEY_CERTIFICATE=data/encryptCert.crt
SIGN_CERTIFICATE=data/signCert.crt
SIGN_KEY=data/signKey.key
TEST_FILE=data/testdoc.txt
TEST_FILE_ENCRYPTED=data/testdoc_enc.txt
TEST_FILE_UNCRYPTED=data/testdoc_dec.txt
SUBMISSION_PLAINTEXT=data/submissionDoc.txt
MAC_PASSWORD=supersecret
SL_DECRYPT=ch.zhaw.securitylab.slcrypt.decrypt.SLDecrypt
SL_ENCRYPT=ch.zhaw.securitylab.slcrypt.encrypt.SLEncrypt
BOUNCYCASTLE_JAR=~/Downloads/bcprov-jdk18on-177.jar

build_submission_file:
	@echo "Encrypting with: AES/CBC/PKCS5Padding"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(SUBMISSION_PLAINTEXT) \
		data/1_AES-CBC-PKCS5Padding_192.txt \
		$(PUBLIC_KEY_CERTIFICATE) \
		AES/CBC/PKCS5Padding \
		192 \
		N
	@echo
	@echo "Encrypting with: AES/GCM/NoPadding with auth/int MAC (HmacSHA512)"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(SUBMISSION_PLAINTEXT) \
		data/2_AES-GCM-NoPadding_256_HMAC-SHA512.txt \
		$(PUBLIC_KEY_CERTIFICATE) \
		AES/GCM/NoPadding \
		256 \
		M \
		HmacSHA512 \
		$(MAC_PASSWORD)
	@echo
	@echo "Encrypting with: SEED/CTR/NoPadding with auth/int MAC (HmacSHA3-512)"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(SUBMISSION_PLAINTEXT) \
		data/3_SEED-CTR-NoPadding_128_HMAC-SHA3-512.txt \
		$(PUBLIC_KEY_CERTIFICATE) \
		SEED/CTR/NoPadding \
		128 \
		M \
		HmacSHA3-512 \
		$(MAC_PASSWORD)
	@echo
	@echo "Encrypting with: RC4 with auth/int Signature (SHA512withRSA)"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(SUBMISSION_PLAINTEXT) \
		data/4_RC4_128_SHA512withRSA.txt \
		$(PUBLIC_KEY_CERTIFICATE) \
		RC4 \
		128 \
		S \
		SHA512withRSA \
		$(SIGN_KEY) \
		$(SIGN_CERTIFICATE)
	@echo
	@echo "Encrypting with: CHACHA20 with auth/int Signature (SHA3-512withRSA)"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(SUBMISSION_PLAINTEXT) \
		data/5_CHACHA20_256_SHA3-512withRSA.txt \
		$(PUBLIC_KEY_CERTIFICATE) \
		CHACHA20 \
		256 \
		S \
		SHA3-512withRSA \
		$(SIGN_KEY) \
		$(SIGN_CERTIFICATE)

test_all: test_aes_cbc_pkcs5 test_aes_gcm test_seed_ctr test_rc4 test_chacha20

test_aes_cbc_pkcs5:
	@echo "Encrypting with: AES/CBC/PKCS5Padding and auth/int via MAC"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(TEST_FILE) \
		$(TEST_FILE_ENCRYPTED) \
		$(PUBLIC_KEY_CERTIFICATE) \
		AES/CBC/PKCS5Padding \
		192 \
		M \
		HmacSHA512 \
		$(MAC_PASSWORD)
	@echo
	@echo
	@echo "Decrypting with: AES/CBC/PKCS5Padding"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_DECRYPT) \
		$(TEST_FILE_ENCRYPTED) \
		$(TEST_FILE_UNCRYPTED) \
		$(PRIVATE_KEY) \
		$(MAC_PASSWORD)
	@cd target/classes; diff $(TEST_FILE) $(TEST_FILE_UNCRYPTED) > /dev/null || \
		echo "AES/CBC/PKCS5Padding ENCRYPTION / DECRYPTION FAILED"
	@echo
	@echo

test_aes_gcm:
	@echo "Encrypting with: AES/GCM/NoPadding and auth/int via MAC"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(TEST_FILE) \
		$(TEST_FILE_ENCRYPTED) \
		$(PUBLIC_KEY_CERTIFICATE) \
		AES/GCM/NoPadding \
		256 \
		M \
		HmacSHA3-512 \
		$(MAC_PASSWORD)
	@echo
	@echo
	@echo "Decrypting with: AES/GCM/NoPadding"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_DECRYPT) \
		$(TEST_FILE_ENCRYPTED) \
		$(TEST_FILE_UNCRYPTED) \
		$(PRIVATE_KEY) \
		$(MAC_PASSWORD)
	@cd target/classes; diff $(TEST_FILE) $(TEST_FILE_UNCRYPTED) > /dev/null || \
		echo "AES/GCM/NoPadding ENCRYPTION / DECRYPTION FAILED"
	@echo
	@echo

test_seed_ctr:
	@echo "Encrypting with: SEED/CTR/NoPadding"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(TEST_FILE) \
		$(TEST_FILE_ENCRYPTED) \
		$(PUBLIC_KEY_CERTIFICATE) \
		SEED/CTR/NoPadding \
		128 \
		S \
		SHA512withRSA \
		$(SIGN_KEY) \
		$(SIGN_CERTIFICATE)
	@echo
	@echo
	@echo "Decrypting with: SEED/CTR/NoPadding"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_DECRYPT) \
		$(TEST_FILE_ENCRYPTED) \
		$(TEST_FILE_UNCRYPTED) \
		$(PRIVATE_KEY)
	@cd target/classes; diff $(TEST_FILE) $(TEST_FILE_UNCRYPTED) > /dev/null || \
		echo "SEED/CTR/NoPadding ENCRYPTION / DECRYPTION FAILED"
	@echo
	@echo

test_rc4:
	@echo "Encrypting with: RC4"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(TEST_FILE) \
		$(TEST_FILE_ENCRYPTED) \
		$(PUBLIC_KEY_CERTIFICATE) \
		RC4 \
		128 \
		S \
		SHA3-512withRSA \
		$(SIGN_KEY) \
		$(SIGN_CERTIFICATE)
	@echo
	@echo
	@echo "Decrypting with: RC4"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_DECRYPT) \
		$(TEST_FILE_ENCRYPTED) \
		$(TEST_FILE_UNCRYPTED) \
		$(PRIVATE_KEY)
	@cd target/classes; diff $(TEST_FILE) $(TEST_FILE_UNCRYPTED) > /dev/null || \
		echo "RC4 ENCRYPTION / DECRYPTION FAILED"
	@echo
	@echo

test_chacha20:
	@echo "Encrypting with: CHACHA20 and no auth/int protection"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_ENCRYPT) \
		$(TEST_FILE) \
		$(TEST_FILE_ENCRYPTED) \
		$(PUBLIC_KEY_CERTIFICATE) \
		CHACHA20 \
		256 \
		N
	@echo
	@echo
	@echo "Decrypting with: CHACHA20"
	@echo
	@cd target/classes; java -cp $(BOUNCYCASTLE_JAR):. \
		$(SL_DECRYPT) \
		$(TEST_FILE_ENCRYPTED) \
		$(TEST_FILE_UNCRYPTED) \
		$(PRIVATE_KEY)
	@cd target/classes; diff $(TEST_FILE) $(TEST_FILE_UNCRYPTED) > /dev/null || \
		echo "CHACHA20 ENCRYPTION / DECRYPTION FAILED"
	@echo
	@echo
