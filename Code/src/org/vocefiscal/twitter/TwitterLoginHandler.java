package org.vocefiscal.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class TwitterLoginHandler 
{
	public static final String consumerKey = "oK5OfonM29VXA5dOfiVam0mLl";
	public static final String consumerSecret = "TKLEgt3Zp5BHTq4NyMZKgH6GjHzfTBpNecVxy58cjdr07q0A8b";

	public TwitterLoginHandler() 
	{

	}

	public boolean startLogin(Context context, String callbackURL )
	{		
		Twitter twitter = new TwitterFactory().getInstance(); 
		twitter.setOAuthConsumer (consumerKey, consumerSecret); 

		boolean ok = true;

		try 
		{
			RequestToken rToken = twitter.getOAuthRequestToken(callbackURL);


			TwitterSession twitterSession = new TwitterSession();

			twitterSession.saveRequest(context, rToken,twitter);

			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rToken.getAuthenticationURL()+"&force_login=true"))); 
		} catch (TwitterException e) 
		{
			ok = false;
		}
		return ok;
	}

	public void completeLogin(final Context context, final String secao, final String zonaEleitoral, final String municipio)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				try 
				{
					final TwitterSession twitterSession = TwitterSession.restore(context);

					if (twitterSession!=null && twitterSession.getOauthverifier() != null) 
					{ 
						//COMMON PART

						final Twitter twitter = new TwitterFactory().getInstance();				

						twitter.setOAuthConsumer (consumerKey, consumerSecret); 

						//retrieve request token
						RequestToken requestToken = new RequestToken(twitterSession.getRtoken(), twitterSession.getRtokensecret());

						final AccessToken at = twitter.getOAuthAccessToken(requestToken, twitterSession.getOauthverifier());

						twitterSession.setTwitter(twitter);

						twitterSession.save(context, at);
						
						
						//TODO - Mover para dentro do Fiscalizacao concluida activity e testar para só compartilhar uma vez
						
						ConfigurationBuilder cb = new ConfigurationBuilder();
						cb.setOAuthConsumerKey(TwitterLoginHandler.consumerKey).setOAuthConsumerSecret(TwitterLoginHandler.consumerSecret).setOAuthAccessToken(at.getToken()).setOAuthAccessTokenSecret(at.getTokenSecret());
						TwitterFactory tf = new TwitterFactory(cb.build());
						Twitter twitterPost = tf.getInstance();

						String tweet = "Eu fiscalizei a seção "+ secao +", na zona eleitoral " +  zonaEleitoral + ", no município de: " +  municipio + " http://www.vocefiscal.org/";

						try 
						{
							twitterPost.updateStatus(tweet);
						} catch (TwitterException e) 
						{							
							e.printStackTrace();
						}

						
					}
				} catch (TwitterException e) 
				{
					e.printStackTrace();
				}
			}
		};
		t.start();			

	}
}
