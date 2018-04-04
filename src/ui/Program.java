package ui;

import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;

import bl.JsonBusiness;
import dal.StatisticaDTO;
import dal.fs.FileSystemReader;
import dal.fs.FileSystemWriter;
import dal.repository.StatisticaRepository;
import model.MetaJson;
import model.MyConfiguration;
import utility.ConfigurationManager;

public class Program {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		Logger log = Logger.getLogger("MainLogger");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT | %4$-7s | %5$s %n");

		// Lettura del file di configurazione
		ConfigurationManager.readConfigFile();
		MyConfiguration config = ConfigurationManager.getConfiguration();

		// Lettura DB
		StatisticaRepository statrepo = new StatisticaRepository(config);
		StatisticaDTO laststats = statrepo.getFirstStatisticaById("35a0f2a1-d085-34b5-ad24-db3dda7b03f0");

		// Lettura da FS
		FileSystemReader reader = new FileSystemReader(config);
		reader.readFileList();
		reader.read();
		List<MetaJson> metajsonlist = reader.getPayload();

		// Core business
		JsonBusiness business = new JsonBusiness(metajsonlist, config);
		business.execute();
		List<JSONObject> jsonlist = business.getOutputJson();

		// Scrittura su DB - dead code
		if (false) {
			for (JSONObject js : jsonlist) {
				statrepo.insertStatistica(js);
			}
		}

		// Scrittura su FS
		if (config.writeOnFs) {
			FileSystemWriter writer = new FileSystemWriter(jsonlist, config);
			writer.writeFiles();
		}
	}
}
