package org.vocefiscal.twitter;

import org.vocefiscal.activities.FiscalizacaoConcluidaActivity;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;


public class TwitterLoginHandler 
{
	public static final String consumerKey = "0qTJSyM6arlUQsJAwcov8o7su";
	public static final String consumerSecret = "gKMOQ5J9Ek0vn8QRA1SZ0yFmSFYbbjFbSRcIMqE31Qs9vmOXmV";
	private Handler handler;


	public TwitterLoginHandler(Handler handler) 
	{
		this.handler = handler;
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
						
						
//						//TODO - Mover para dentro do Fiscalizacao concluida activity e testar para só compartilhar uma vez
//						
//						ConfigurationBuilder cb = new ConfigurationBuilder();
//						cb.setOAuthConsumerKey(TwitterLoginHandler.consumerKey).setOAuthConsumerSecret(TwitterLoginHandler.consumerSecret).setOAuthAccessToken(at.getToken()).setOAuthAccessTokenSecret(at.getTokenSecret());
//						TwitterFactory tf = new TwitterFactory(cb.build());
//						Twitter twitterPost = tf.getInstance();
//
//						String tweet = "Eu fiscalizei a seção "+ secao +", na zona eleitoral " +  zonaEleitoral + ", no município de: " +  municipio + " http://www.vocefiscal.org/";

//						try 
//						{
//							twitterPost.updateStatus(tweet);
//							
//							handler.post(new Runnable() 
//							{
//								
//								@Override
//								public void run() 
//								{
//									Toast.makeText(context,"Tweet feito com sucesso!",Toast.LENGTH_SHORT).show();
//									
//								}
//							});
//						} catch (TwitterException e) 
//						{							
//							handler.post(new Runnable() 
//							{
//								
//								@Override
//								public void run() 
//								{
//									Toast.makeText(context,"Não foi possível twittar!",Toast.LENGTH_SHORT).show();
//								}
//							});
//						}

						
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
