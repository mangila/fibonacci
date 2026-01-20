import { useState } from "react";
import {
  queryForList,
  subscribeLivestream,
  unsubscribeLivestream,
} from "../_utils/api";
import { useUpdateEffect } from "ahooks";

interface Props {
  username: string;
}

const DatasourceRadio = ({ username }: Props) => {
  const [livestream, setLivestream] = useState(false);

  const handleLivestream = () => {
    if (!livestream) {
      setLivestream(true);
    }
  };

  const handleQuery = () => {
    setLivestream(false);
    queryForList(username);
  };

  const handleReset = () => {
    if (livestream) {
      setLivestream(false);
    }
  };

  useUpdateEffect(() => {
    if (livestream) {
      subscribeLivestream(username);
    }
    if (!livestream) {
      unsubscribeLivestream(username);
    }
  }, [livestream]);

  return (
    <>
      <form
        className="flex justify-center space-x-4 mt-6 text-xl gap-4 mb-4"
        onReset={handleReset}
      >
        <input
          className="btn"
          type="radio"
          name="frameworks"
          aria-label="Livestream"
          onClick={handleLivestream}
        />
        <input
          className="btn"
          type="radio"
          name="frameworks"
          aria-label="Query"
          onClick={handleQuery}
        />
        <input className="btn btn-square" type="reset" value="×" />
      </form>
    </>
  );
};

export default DatasourceRadio;
